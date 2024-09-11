package no.nav.tiltakspenger.soknad.api

import arrow.core.Either
import arrow.core.right
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.routing.routing
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.nav.security.token.support.v2.asIssuerProps
import no.nav.tiltakspenger.libs.common.AccessToken
import no.nav.tiltakspenger.libs.jobber.LeaderPodLookup
import no.nav.tiltakspenger.libs.jobber.LeaderPodLookupClient
import no.nav.tiltakspenger.libs.jobber.LeaderPodLookupFeil
import no.nav.tiltakspenger.libs.jobber.RunCheckFactory
import no.nav.tiltakspenger.soknad.api.antivirus.AvClient
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.antivirus.AvServiceImpl
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.db.flywayMigrate
import no.nav.tiltakspenger.soknad.api.featuretoggling.setupUnleash
import no.nav.tiltakspenger.soknad.api.health.healthRoutes
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.jobber.TaskExecutor
import no.nav.tiltakspenger.soknad.api.metrics.MetricsCollector
import no.nav.tiltakspenger.soknad.api.metrics.metricRoutes
import no.nav.tiltakspenger.soknad.api.pdf.PdfClient
import no.nav.tiltakspenger.soknad.api.pdf.PdfServiceImpl
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.pdl.pdlRoutes
import no.nav.tiltakspenger.soknad.api.soknad.jobb.person.PersonHttpklient
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepo
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepoImpl
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.soknad.SøknadServiceImpl
import no.nav.tiltakspenger.soknad.api.soknad.jobb.SøknadJobbService
import no.nav.tiltakspenger.soknad.api.soknad.jobb.SøknadJobbServiceImpl
import no.nav.tiltakspenger.soknad.api.soknad.søknadRoutes
import no.nav.tiltakspenger.soknad.api.soknad.validateSøknad
import no.nav.tiltakspenger.soknad.api.tiltak.TiltakService
import no.nav.tiltakspenger.soknad.api.tiltak.tiltakRoutes
import java.util.UUID.randomUUID

fun main(args: Array<String>) {
    System.setProperty("logback.configurationFile", Configuration.logbackConfigurationFile())
    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    DefaultExports.initialize()

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.soknadApi(metricsCollector: MetricsCollector = MetricsCollector()) {
    val log = KotlinLogging.logger {}
    log.info { "starting server" }

    flywayMigrate()
    installCallLogging()
    installAuthentication()
    installJacksonFeature()
    install(RequestValidation) {
        validateSøknad()
    }

    val unleash = setupUnleash(environment = environment)
    log.info { "Unleash server url er: ${environment.config.property("unleash.unleash_server_api_url").getString()}" }
    log.info { "Redirect feature er enabled: ${unleash.isEnabled("REDIRECT_TIL_GAMMEL_SOKNAD")}" }

    val pdlEndpoint = environment.config.property("endpoints.pdl").getString()
    val pdlScope = environment.config.property("scope.pdl").getString()
    val oauth2CredentialsClient = checkNotNull(ClientConfig(environment.config, httpClientWithRetry()).clients["azure"])
    val personGateway =
        PersonHttpklient(
            pdlEndpoint,
        ) {
            AccessToken(
                oauth2CredentialsClient.clientCredentials(pdlScope).accessToken
                    ?: throw IllegalStateException("Responsen fra token-exchange mangler accessToken"),
            )
        }
    val pdlService = PdlService(environment.config)
    val søknadService: SøknadService = SøknadServiceImpl(
        pdfService = PdfServiceImpl(
            PdfClient(
                config = environment.config,
                client = httpClientCIO(timeout = 30L),
            ),
        ),
        joarkService = JoarkService(environment.config),
    )
    val søknadJobbService = SøknadJobbServiceImpl(personGateway)
    val avService: AvService = AvServiceImpl(
        av = AvClient(
            config = environment.config,
            client = httpClientCIO(timeout = 30L),
        ),
    )
    val tiltakService = TiltakService(environment.config)
    val søknadRepo = SøknadRepoImpl()


    setupRouting(
        pdlService = pdlService,
        søknadService = søknadService,
        tiltakService = tiltakService,
        søknadRepo = søknadRepo,
        avService = avService,
        metricsCollector = metricsCollector,
    )

    val runCheckFactory =
        if (Configuration.isNais()) {
            RunCheckFactory(
                leaderPodLookup =
                LeaderPodLookupClient(
                    electorPath = Configuration.electorPath(),
                    logger = KotlinLogging.logger { },
                ),
            )
        } else {
            RunCheckFactory(
                leaderPodLookup =
                object : LeaderPodLookup {
                    override fun amITheLeader(localHostName: String): Either<LeaderPodLookupFeil, Boolean> =
                        true.right()
                },
            )
        }
    val stoppableTasks =
        TaskExecutor.startJob(
            runCheckFactory = runCheckFactory,
            tasks =
            listOf { correlationId ->
                søknadJobbService.journalførLagredeSøknader(correlationId)
            },
        )

    environment.monitor.subscribe(ApplicationStarted) {
        log.info { "Starter server" }
    }
    environment.monitor.subscribe(ApplicationStopped) {
        stoppableTasks.stop()
        log.info { "Stopper server" }
    }
}

internal fun Application.setupRouting(
    pdlService: PdlService,
    søknadService: SøknadService,
    tiltakService: TiltakService,
    søknadRepo: SøknadRepo,
    avService: AvService,
    metricsCollector: MetricsCollector,
) {
    val issuers = environment.config.asIssuerProps().keys
    routing {
        authenticate(*issuers.toTypedArray()) {
            pdlRoutes(
                pdlService = pdlService,
                tiltakService = tiltakService,
                metricsCollector = metricsCollector,
            )
            søknadRoutes(
                søknadService = søknadService,
                avService = avService,
                pdlService = pdlService,
                metricsCollector = metricsCollector,
                søknadRepo = søknadRepo,
            )
            tiltakRoutes(
                tiltakService = tiltakService,
                metricsCollector = metricsCollector,
                pdlService = pdlService,
            )
        }
        healthRoutes(emptyList()) // TODO: Relevante helsesjekker
        metricRoutes()
    }
}

internal fun Application.installJacksonFeature() {
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            registerModule(JavaTimeModule())
            registerModule(KotlinModule.Builder().build())
        }
    }
}

internal fun Application.installCallLogging() {
    install(CallId) {
        generate { randomUUID().toString() }
    }
    install(CallLogging) {
        callIdMdc("call-id")
        filter { call ->
            call.request.path().startsWith("/$SØKNAD_PATH")
            call.request.path().startsWith("/$PERSONALIA_PATH")
        }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val req = call.request
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent req: $req"
        }
    }
}
