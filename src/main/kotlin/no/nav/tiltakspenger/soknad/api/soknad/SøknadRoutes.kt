package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.SØKNAD_PATH
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

val LOG = KotlinLogging.logger { }

fun Route.søknadRoutes(
    søknadService: SøknadService,
) {
    route(SØKNAD_PATH) {
        post {
            val vedlegg = mutableListOf<Vedlegg>()
            kotlin.runCatching {
                val multipartData = call.receiveMultipart()

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            LOG.info { part.value }
                        }

                        is PartData.FileItem -> {
                            val fileBytes = part.streamProvider().readBytes()
                            vedlegg.add(Vedlegg(filnavn = part.originalFileName!!, dokument = fileBytes))
                            LOG.info { part.originalFileName }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                val søknad = call.receive<Søknad>()
                val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
                val journalpostId = runBlocking {
                    søknadService.opprettDokumenterOgArkiverIJoark(søknad, fødselsnummer, vedlegg)
                }

                call.respondText(status = HttpStatusCode.Created, text = journalpostId)
            }.onFailure {
                when (it) {
                    is CannotTransformContentToTypeException, is BadRequestException -> {
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
//            try {
//                val søknad = call.receive<Søknad>()
//                val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
//                runBlocking {
//                    søknadService.lagPdfOgSendTilJoark(søknad, fødselsnummer)
//                }
//
//                call.respondText(status = HttpStatusCode.NoContent, text = "OK")
//            } catch (exception: Exception) {
//                when (exception) {
//                    is CannotTransformContentToTypeException, is BadRequestException -> {
//                        LOG.error("Ugyldig søknad", exception)
//                        call.respondText(
//                            text = "Bad Request",
//                            contentType = ContentType.Text.Plain,
//                            status = HttpStatusCode.BadRequest,
//                        )
//                    }
//                    else -> {
//                        LOG.error("Noe gikk galt ved post av søknad", exception)
//                        call.respondText(
//                            text = "Internal server error",
//                            contentType = ContentType.Text.Plain,
//                            status = HttpStatusCode.InternalServerError,
//                        )
//                    }
//                }
//            }
        }
    }.also { LOG.info { "satt opp endepunkt /soknad" } }
}
