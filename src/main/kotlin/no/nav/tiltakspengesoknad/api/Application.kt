package no.nav.tiltakspengesoknad.api

import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.routing.routing
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.auth.installAuthentication
import no.nav.tiltakspengesoknad.api.health.healthRoutes
import no.nav.tiltakspengesoknad.api.soknad.soknadRoutes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    install(ContentNegotiation) {
        jackson {
            // TODO: Sett opp Jackson
        }
    }

    // Til debugging enn så lenge
    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }

  /*install(RequestValidation) {
      TODO: Backendvalidering av søknad
  }*/

    val config = this.environment.config

    installAuthentication(config)

    wireRoutes()

    environment.monitor.subscribe(ApplicationStarted) {
        log.info { "Starter server" }
    }
    environment.monitor.subscribe(ApplicationStopped) {
        log.info { "Stopper server" }
    }
}

fun Application.wireRoutes() {
  /*if (local()) {
      //TODO: Mocking
  }*/

    // val pdlService = PdlService()

    routing {
        authenticate("userTest") {
            // TODO pdlRoutes(pdlService)
            soknadRoutes()
        }

        healthRoutes(emptyList())
    }
}
