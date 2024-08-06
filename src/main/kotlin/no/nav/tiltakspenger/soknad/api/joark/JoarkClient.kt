package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.httpClientWithRetry
import no.nav.tiltakspenger.soknad.api.objectMapper
import no.nav.tiltakspenger.soknad.api.pdl.INDIVIDSTONAD
import org.slf4j.LoggerFactory

internal const val joarkPath = "rest/journalpostapi/v1/journalpost"

class JoarkClient(
    private val config: ApplicationConfig,
    private val client: HttpClient = httpClientWithRetry(timeout = 30L),
    private val joarkCredentialsClient: JoarkCredentialsClient = JoarkCredentialsClient(config),
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val securelog = KotlinLogging.logger("tjenestekall")

    private val joarkEndpoint = config.property("endpoints.joark").getString()

    suspend fun opprettJournalpost(
        dokumentInnhold: Journalpost,
        callId: String,
    ): String {
        try {
            log.info("Henter credentials for å arkivere i Joark")
            val token = joarkCredentialsClient.getToken()
            log.info("Hent credentials til arkiv OK. Starter journalføring av søknad")
            val res = client.post("$joarkEndpoint/$joarkPath") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", INDIVIDSTONAD)
                header("Nav-Callid", callId)
                parameter("forsoekFerdigstill", false)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(
                    objectMapper.writeValueAsString(
                        JournalpostRequest(
                            tittel = dokumentInnhold.tittel,
                            journalpostType = dokumentInnhold.journalpostType,
                            tema = dokumentInnhold.tema,
                            kanal = dokumentInnhold.kanal,
                            behandlingstema = dokumentInnhold.behandlingstema,
                            // journalfoerendeEnhet = dokumentInnhold.journalfoerendeEnhet,
                            avsenderMottaker = dokumentInnhold.avsenderMottaker,
                            bruker = dokumentInnhold.bruker,
                            // sak = dokumentInnhold.sak,
                            dokumenter = dokumentInnhold.dokumenter,
                            eksternReferanseId = callId,
                        ),
                    ),
                )
            }

            when (val status = res.status) {
                HttpStatusCode.Created -> {
                    val response = res.call.body<JoarkResponse>()

                    val journalpostId = if (response.journalpostId.isNullOrEmpty()) {
                        log.error("Fikk 201 Created fra Joark, men vi fikk ingen journalpostId. response=$response")
                        throw IllegalStateException("Fikk 201 Created fra Joark, men vi fikk ingen journalpostId. response=$response")
                    } else {
                        response.journalpostId
                    }

                    // if ((response.journalpostferdigstilt == null) || (response.journalpostferdigstilt == false)) {
                    //     log.error("Kunne ikke ferdigstille journalføring for journalpostId: $journalpostId. response=$response")
                    //     throw IllegalStateException("Kunne ikke ferdigstille journalføring for journalpostId: $journalpostId. response=$response")
                    // }

                    log.info("Vi har opprettet journalpost med id: $journalpostId")
                    return journalpostId
                }

                else -> {
                    val body = res.bodyAsText()
                    log.error("Fikk respons fra Joark, men forventet 201 CREATED. Status: $status, body: $body")
                    throw RuntimeException("Fikk respons fra Joark, men forventet 201 CREATED. Status: $status, body: $body")
                }
            }
        } catch (throwable: Throwable) {
            if (throwable is ClientRequestException && throwable.response.status == HttpStatusCode.Conflict) {
                log.warn("Søknaden har allerede blitt journalført (409 Conflict)")
                val response = throwable.response.call.body<JoarkResponse>()
                return response.journalpostId.orEmpty()
            }
            if (throwable is IllegalStateException) {
                log.error("Vi fikk en IllegalStateException i JoarkClient", throwable)
                throw throwable
            } else {
                log.error("JoarkClient: Fikk en ukjent exception.", throwable)
                throw RuntimeException("JoarkClient: Fikk en ukjent exception.", throwable)
            }
        }
    }

    data class JoarkResponse(
        val journalpostId: String?,
        val journalpostferdigstilt: Boolean?,
        val dokumenter: List<Dokumenter>?,
    )

    data class Dokumenter(
        val dokumentInfoId: String?,
        val tittel: String?,
    )
}
