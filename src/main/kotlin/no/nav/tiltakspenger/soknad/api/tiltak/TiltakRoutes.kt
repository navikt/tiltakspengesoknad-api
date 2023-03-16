package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.token

fun Route.tiltakRoutes(tiltakService: TiltakService) {
    val secureLog = KotlinLogging.logger("tjenestekall")

    get(path = "/tiltak") {
        try {
            val fødselsnummer = call.fødselsnummer()
            val subjectToken = call.token()
            if (fødselsnummer == null) {
                throw IllegalStateException("Mangler fødselsnummer")
            }
            val tiltakDto = tiltakService.hentTiltak(subjectToken = subjectToken)
            call.respond(tiltakDto)
        } catch (e: Exception) {
            secureLog.error { e.stackTraceToString() }
            call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error")
        }
    }
}
