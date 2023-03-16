package no.nav.tiltakspenger.soknad.api.soknad

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
import no.nav.tiltakspenger.soknad.api.SØKNAD_PATH
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import java.lang.Exception
import java.util.*

val LOG = KotlinLogging.logger { }

data class Person(
    val fnr: String,
    val fornavn: String,
    val mellomnavn: String,
    val etternavn: String,
)

fun Route.søknadRoutes(
    søknadService: SøknadService,
) {
    route(SØKNAD_PATH) {
        post {
            try {
                val søknad = call.receive<Søknad>()
                val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
                søknadService.lagPdfOgSendTilJoark(søknad, fødselsnummer)

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
