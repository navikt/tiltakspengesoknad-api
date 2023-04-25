package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.PERSONALIA_PATH
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.token

val secureLog = KotlinLogging.logger("tjenestekall")

fun Route.pdlRoutes(pdlService: PdlService) {
    get(path = PERSONALIA_PATH) {
        LOG.info("Vi kom oss inn i GET på /personalia endepunktet!")
        try {
            val fødselsnummer = call.fødselsnummer()
            val subjectToken = call.token()
            LOG.info("Hentet ut token og fnr fra kallet")

            if (fødselsnummer == null) {
                throw IllegalStateException("Mangler fødselsnummer")
            }
            LOG.info("Prøver å hente personalia fra PDL")
            val personDTO = pdlService.hentPersonaliaMedBarn(fødselsnummer = fødselsnummer, subjectToken = subjectToken)
            LOG.info("Vi fikk hentet personalia fra PDL")
            call.respond(personDTO)
        } catch (e: Exception) {
            secureLog.error { e.stackTraceToString() }
            call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error")
        }
    }
}
