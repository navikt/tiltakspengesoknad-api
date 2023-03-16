package no.nav.tiltakspenger.soknad.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.request.httpMethod
import io.ktor.server.routing.routing
import mu.KotlinLogging
import no.nav.security.token.support.v2.asIssuerProps
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.health.healthRoutes
import no.nav.tiltakspenger.soknad.api.joark.JoarkClient
import no.nav.tiltakspenger.soknad.api.joark.JoarkServiceImpl
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

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.soknadApi(
    pdlService: PdlService = PdlService(environment.config),
    søknadService: SøknadService = SøknadServiceImpl(
        pdfService = PdfServiceImpl(
            PdfClient(
                config = environment.config,
                client = httpClientCIO(),
            ),
        ),
        joarkService = JoarkServiceImpl(
            joark = JoarkClient(
                config = environment.config,
                client = httpClientCIO(),
            ),
        ),
    ),
    tiltakService: TiltakService = TiltakService(environment.config),
) {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
    log.info { "starting server" }

    // Til debugging enn så lenge
    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }

    installAuthentication()
    setupRouting(
        pdlService = pdlService,
        søknadService = søknadService,
        tiltakService = tiltakService,
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
) {
    val issuers = environment.config.asIssuerProps().keys
    routing {
        authenticate(*issuers.toTypedArray()) {
            pdlRoutes(pdlService = pdlService)
            søknadRoutes(søknadService = søknadService)
            tiltakRoutes(tiltakService = tiltakService)
        }
        healthRoutes(emptyList()) // TODO: Relevante helsesjekker
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
