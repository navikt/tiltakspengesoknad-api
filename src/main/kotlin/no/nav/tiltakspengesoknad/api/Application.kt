package no.nav.tiltakspengesoknad.api

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.health.healthRoutes
import no.nav.tiltakspengesoknad.api.soknad.søknadRoutes

fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
    log.info { "starting server" }

    val server = embeddedServer(Netty, Configuration.applicationPort(), module = Application::søknadModule).start(wait = true)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            log.info { "Stopper server" }
            server.stop(gracePeriodMillis = 3000, timeoutMillis = 3000)
        },
    )
}

fun Application.søknadModule() {
    setupRouting()
    installJacksonFeature()
}

internal fun Application.setupRouting() {
    routing {
        søknadRoutes()
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
