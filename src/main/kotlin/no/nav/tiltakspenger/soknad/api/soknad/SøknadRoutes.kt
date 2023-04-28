package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
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
import no.nav.tiltakspenger.soknad.api.acr
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.antivirus.Status
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.fødselsnummer
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.token
import no.nav.tiltakspenger.soknad.api.util.sjekkContentType
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

val LOG = KotlinLogging.logger { }

fun Route.søknadRoutes(
    søknadService: SøknadService,
    avService: AvService,
    pdlService: PdlService,
) {
    route(SØKNAD_PATH) {
        post {
            val vedleggListe = mutableListOf<Vedlegg>()
            kotlin.runCatching {
                val multipartData = call.receiveMultipart()
                var søknad: SøknadRequest? = null

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "søknad") {
                                try {
                                    søknad = deserialize(part.value)
                                } catch (e: Exception) {
                                    LOG.error("Ugyldig søknadsformat", e)
                                    call.respondText(
                                        text = "Bad Request",
                                        contentType = ContentType.Text.Plain,
                                        status = HttpStatusCode.BadRequest,
                                    )
                                }
                            } else {
                                LOG.error { "Recieved multipart form with unknown key ${part.name}" }
                            }
                        }

                        is PartData.FileItem -> {
                            val filnavn = part.originalFileName ?: "untitled-${part.hashCode()}"
                            val fileBytes = part.streamProvider().readBytes()
                            val vedlegg = Vedlegg(filnavn = filnavn, contentType = sjekkContentType(fileBytes), dokument = fileBytes)
                            vedleggListe.add(vedlegg)
                            LOG.info { part.originalFileName }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                if (søknad == null) {
                    call.respondText(status = HttpStatusCode.BadRequest, text = "Bad request")
                } else {
                    val fødselsnummer = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
                    LOG.info { "${call.acr()}" }
                    val subjectToken = call.token()
                    val person = pdlService.hentPersonaliaMedBarn(fødselsnummer, subjectToken)
                    val journalpostId = runBlocking {
                        val resultat = avService.scan(vedleggListe) // TODO: Test av av! Skriv om.
                        resultat.forEach {
                            LOG.info { "${it.filnavn}: ${it.resultat}" }
                        }
                        if (resultat.any { it.resultat == Status.FOUND }) {
                            call.respondText(
                                text = "Skadevare funnet i vedlegg",
                                contentType = ContentType.Text.Plain,
                                status = HttpStatusCode.BadRequest,
                            )
                        }
                        søknadService.opprettDokumenterOgArkiverIJoark(søknad!!, fødselsnummer, person, vedleggListe)
                    }
                    call.respondText(status = HttpStatusCode.Created, text = journalpostId)
                }
            }.onFailure {
                when (it) {
                    is CannotTransformContentToTypeException, is BadRequestException, is BadExtensionException -> {
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

class BadExtensionException(message: String) : RuntimeException(message)
