package no.nav.tiltakspengesoknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.SØKNAD_PATH
import no.nav.tiltakspengesoknad.api.domain.Søknad
import java.lang.Exception

val LOG = KotlinLogging.logger { }

fun Route.søknadRoutes() {
    route(SØKNAD_PATH) {
        post {
            try {
                call.receive<Søknad>()
                call.respondText(status = HttpStatusCode.NoContent, text = "OK")
            } catch (exception: Exception) {
                when (exception) {
                    is CannotTransformContentToTypeException, is BadRequestException -> {
                        LOG.error("Ugyldig søknad", exception)
                        call.respondText(
                            text = "Bad Request",
                            contentType = ContentType.Text.Plain,
                            status = HttpStatusCode.BadRequest,
                        )
                    }
                    else -> {
                        LOG.error("Noe gikk galt ved post av søknad", exception)
                        call.respondText(
                            text = "Internal server error",
                            contentType = ContentType.Text.Plain,
                            status = HttpStatusCode.InternalServerError,
                        )
                    }
                }
            }
        }
    }.also { LOG.info { "satt opp endepunkt /soknad" } }
}
