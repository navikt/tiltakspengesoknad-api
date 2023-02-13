package no.nav.tiltakspengesoknad.api

import mu.KotlinLogging
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import no.nav.tiltakspengesoknad.api.soknad.soknadRoutes

fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")

    val port = 8081 //TODO: Remove from here. =)

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
    log.info { "starting server" }

    val server = embeddedServer(Netty, port) {
        routing {
            soknadRoutes()
        }
    }.start(wait = true)

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info { "Stopper server" }
        server.stop(gracePeriodMillis = 3000, timeoutMillis = 3000)
    })
}
