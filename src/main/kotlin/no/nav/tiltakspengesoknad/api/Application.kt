package no.nav.tiltakspengesoknad.api

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.health.healthRoutes
import no.nav.tiltakspengesoknad.api.soknad.soknadRoutes

fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
    log.info { "starting server" }

    val server = embeddedServer(Netty, Configuration.applicationPort()) {
        routing {
            soknadRoutes()
            healthRoutes(emptyList()) // TODO: Relevante helsesjekker
        }
    }.start(wait = true)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            log.info { "Stopper server" }
            server.stop(gracePeriodMillis = 3000, timeoutMillis = 3000)
        },
    )
}
