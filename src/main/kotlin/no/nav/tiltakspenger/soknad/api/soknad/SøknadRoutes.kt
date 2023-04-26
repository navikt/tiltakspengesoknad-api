package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.SØKNAD_PATH
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.antivirus.MalwareFoundException
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.token

val LOG = KotlinLogging.logger { }

fun Route.søknadRoutes(
    søknadService: SøknadService,
    avService: AvService,
    pdlService: PdlService,
) {
    route(SØKNAD_PATH) {
        post {
            kotlin.runCatching {
                val (søknad, vedlegg) = søknadService.taInnSøknadSomMultipart(call.receiveMultipart())
                avService.gjørVirussjekkAvVedlegg(vedlegg)
                val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
                val subjectToken = call.token()
                val person = pdlService.hentPersonaliaMedBarn(fødselsnummer, subjectToken)
                val journalpostId = runBlocking {
                    søknadService.opprettDokumenterOgArkiverIJoark(søknad, fødselsnummer, person, vedlegg)
                }
                call.respondText(status = HttpStatusCode.Created, text = journalpostId)
            }.onFailure {
                when (it) {
                    is CannotTransformContentToTypeException,
                    is BadRequestException,
                    is BadExtensionException,
                    is MissingContentException,
                    is UnrecognizedFormItemException,
                    is MalwareFoundException,
                    -> {
                        LOG.error("Ugyldig søknad", it)
                        call.respondText(
                            text = "Bad Request",
                            contentType = ContentType.Text.Plain,
                            status = HttpStatusCode.BadRequest,
                        )
                    }
                    else -> {
                        LOG.error("Noe gikk galt ved post av søknad", it)
                        call.respondText(
                            text = "Internal server error",
                            contentType = ContentType.Text.Plain,
                            status = HttpStatusCode.InternalServerError,
                        )
                    }
                }
            }.getOrThrow()
        }
    }.also { LOG.info { "satt opp endepunkt /soknad" } }
}

class BadExtensionException(message: String) : RuntimeException(message)
