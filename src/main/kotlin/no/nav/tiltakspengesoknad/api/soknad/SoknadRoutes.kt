package no.nav.tiltakspengesoknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.SØKNAD_URL

val LOG = KotlinLogging.logger { }

fun Route.soknadRoutes() {
    route(SØKNAD_URL) {
        get {
            call.respondText(
                text = "Dette er en test!",
                contentType = ContentType.Text.Plain,
                status = HttpStatusCode.OK,
            )
        }
    }.also { LOG.info { "satt opp endepunkt /soknad" } }
}
