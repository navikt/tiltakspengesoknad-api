package no.nav.tiltakspengesoknad.api.health

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import mu.KotlinLogging

val LOG = KotlinLogging.logger { }

fun Route.healthRoutes(healthChecks: List<HealthCheck>) {
    route("/isalive") {
        get {
            val failedHealthChecks = healthChecks.filter { it.status() == HealthStatus.UNHEALTHY }
            if (failedHealthChecks.isNotEmpty()) {
                LOG.warn { "Failed health checks: $failedHealthChecks" }
                call.respondText(
                    text = "DEAD",
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.ServiceUnavailable,
                )
            } else {
                call.respondText(
                    text = "ALIVE",
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.OK,
                )
            }
        }
    }.also { LOG.info { "satt opp endepunkt /isalive" } }
    route("/isready") {
        get {
            call.respondText(text = "READY", contentType = ContentType.Text.Plain)
        }
    }.also { LOG.info { "satt opp endepunkt /isready" } }
}
