package no.nav.tiltakspenger.soknad.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.nav.security.token.support.v2.asIssuerProps
import no.nav.tiltakspenger.soknad.api.antivirus.AvClient
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.antivirus.AvServiceImpl
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.health.healthRoutes
import no.nav.tiltakspenger.soknad.api.joark.JoarkClient
import no.nav.tiltakspenger.soknad.api.joark.JoarkServiceImpl
import no.nav.tiltakspenger.soknad.api.joark.TokenServiceImpl
import no.nav.tiltakspenger.soknad.api.metrics.MetricsCollector
import no.nav.tiltakspenger.soknad.api.metrics.metricRoutes
import no.nav.tiltakspenger.soknad.api.pdf.PdfClient
import no.nav.tiltakspenger.soknad.api.pdf.PdfServiceImpl
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.pdl.pdlRoutes
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.soknad.SøknadServiceImpl
import no.nav.tiltakspenger.soknad.api.soknad.søknadRoutes
import no.nav.tiltakspenger.soknad.api.soknad.validateSøknad
import no.nav.tiltakspenger.soknad.api.tiltak.TiltakService
import no.nav.tiltakspenger.soknad.api.tiltak.tiltakRoutes

fun main(args: Array<String>) {
    System.setProperty("logback.configurationFile", "egenLogback.xml")
    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    DefaultExports.initialize()

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.soknadApi(
    pdlService: PdlService = PdlService(environment.config),
    søknadService: SøknadService = SøknadServiceImpl(
        pdfService = PdfServiceImpl(
            PdfClient(
                config = environment.config,
                client = httpClientCIO(timeout = 10L),
            ),
        ),
        joarkService = JoarkServiceImpl(
            joark = JoarkClient(
                config = environment.config,
                client = httpClientCIO(timeout = 30L),
                tokenService = TokenServiceImpl(),
            ),
        ),
    ),
    avService: AvService = AvServiceImpl(
        av = AvClient(
            config = environment.config,
            client = httpClientCIO(timeout = 30L),
        ),
    ),
    tiltakService: TiltakService = TiltakService(environment.config),
    metricsCollector: MetricsCollector = MetricsCollector(),
) {
    val log = KotlinLogging.logger {}
    log.info { "starting server" }

    // Til debugging enn så lenge
    install(CallLogging) {
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

    installAuthentication()
    setupRouting(
        pdlService = pdlService,
        søknadService = søknadService,
        tiltakService = tiltakService,
        avService = avService,
        metricsCollector = metricsCollector,
    )
    installJacksonFeature()

    install(RequestValidation) {
        validateSøknad()
    }

    environment.monitor.subscribe(ApplicationStarted) {
        log.info { "Starter server" }
    }
    environment.monitor.subscribe(ApplicationStopped) {
        log.info { "Stopper server" }
    }
}

internal fun Application.setupRouting(
    pdlService: PdlService,
    søknadService: SøknadService,
    tiltakService: TiltakService,
    avService: AvService,
    metricsCollector: MetricsCollector,
) {
    val issuers = environment.config.asIssuerProps().keys
    routing {
        authenticate(*issuers.toTypedArray()) {
            pdlRoutes(
                pdlService = pdlService,
                metricsCollector = metricsCollector,
            )
            søknadRoutes(
                søknadService = søknadService,
                avService = avService,
                pdlService = pdlService,
                metricsCollector = metricsCollector,
            )
            tiltakRoutes(
                tiltakService = tiltakService,
                metricsCollector = metricsCollector,
            )
        }
        healthRoutes(emptyList()) // TODO: Relevante helsesjekker
        metricRoutes()

        // Testkode for å feile CodeQL
        post("/testcodeql") {
            val username = call.receiveParameters()["username"]
            val password = call.receiveParameters()["password"]

            val loginSuccessful = loginUser(username, password)

            if (loginSuccessful) {
                call.respondText("Login successful")
            } else {
                call.respondText("Login failed")
            }
        }

    }
}

// Testkode for å feile CodeQL
fun loginUser(username: String?, password: String?): Boolean {
    // Ingen sanitering/validering
    if (username != null && password != null) {
        if (username == "admin" && password == "password") {
            return true
        }
    }
    return false
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
