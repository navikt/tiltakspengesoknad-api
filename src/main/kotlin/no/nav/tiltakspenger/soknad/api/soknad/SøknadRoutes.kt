package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.SØKNAD_PATH
import no.nav.tiltakspenger.soknad.api.acr
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.antivirus.MalwareFoundException
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.token
import java.time.LocalDateTime

val LOG = KotlinLogging.logger { }

fun Route.søknadRoutes(
    søknadService: SøknadService,
    avService: AvService,
    pdlService: PdlService,
) {
    post(SØKNAD_PATH) {
        try {
            val innsendingTidspunkt = LocalDateTime.now()
            val (søknad, vedlegg) = søknadService.taInnSøknadSomMultipart(call.receiveMultipart())
            avService.gjørVirussjekkAvVedlegg(vedlegg)
            val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
            val acr = call.acr() ?: "Ingen Level"
            val subjectToken = call.token()
            val person = pdlService.hentPersonaliaMedBarn(fødselsnummer, subjectToken)

            val journalpostId =
                søknadService.opprettDokumenterOgArkiverIJoark(
                    søknad,
                    fødselsnummer,
                    person,
                    vedlegg,
                    acr,
                    innsendingTidspunkt,
                )
            val søknadResponse = SøknadResponse(
                journalpostId = journalpostId,
                innsendingTidspunkt = innsendingTidspunkt,
            )
            call.respond(status = HttpStatusCode.Created, message = søknadResponse)
        } catch (exception: Exception) {
            when (exception) {
                is CannotTransformContentToTypeException,
                is BadRequestException,
                is MissingContentException,
                is UnrecognizedFormItemException,
                is MalwareFoundException,
                is UninitializedPropertyAccessException,
                is RequestValidationException,
                -> {
                    LOG.error("Ugyldig søknad ${exception.message}", exception)
                    call.respondText(
                        text = "Bad Request",
                        contentType = ContentType.Text.Plain,
                        status = HttpStatusCode.BadRequest,
                    )
                }

                else -> {
                    LOG.error("Noe gikk galt ved post av søknad ${exception.message}", exception)
                    call.respondText(
                        text = "Internal server error",
                        contentType = ContentType.Text.Plain,
                        status = HttpStatusCode.InternalServerError,
                    )
                }
            }
        }
    }.also { LOG.info { "satt opp endepunkt /soknad" } }
}
