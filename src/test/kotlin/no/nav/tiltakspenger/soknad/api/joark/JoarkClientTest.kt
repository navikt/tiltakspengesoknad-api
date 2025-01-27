package no.nav.tiltakspenger.soknad.api.joark

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.libs.common.AccessToken
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.httpClientGeneric
import no.nav.tiltakspenger.soknad.api.soknad.validering.søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JoarkClientTest {
    private val journalpostId = "1"
    private val søknadId = SøknadId.random()
    private val baseurl = "http://dokarkiv"

    @Test
    fun `joark svarer alt ok`() {
        val mock = MockEngine {
            respond(
                content = okSvarJoark,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
                søknadId = søknadId,
                callId = "123",
            )

            resp shouldBe journalpostId
        }
    }

    @Test
    fun `joark svarer ikke ferdigstilt`() {
        val mock = MockEngine {
            respond(
                content = svarIkkeFerdigstiltJoark,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

//        val joarkResonse = JoarkClient.JoarkResponse(
//            journalpostId = journalpostId,
//            journalpostferdigstilt = false,
//            dokumenter = listOf(dokumentResponse),
//        )

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
                søknadId = søknadId,
                callId = "123",
            )

            resp shouldBe journalpostId
        }
    }

    @Test
    fun `hvis joark svarer med 409 Conflict returnerer opprettJournalpost en journalpostId hvis vi har fått en`() {
        val mock = MockEngine {
            respond(
                content = okSvarJoark,
                status = HttpStatusCode.Conflict,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
                søknadId = søknadId,
                callId = "123",
            )
            resp shouldBe journalpostId
        }
    }

    @Test
    fun `joark svarer med feil`() {
        val mock = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            shouldThrow<RuntimeException> {
                joarkClient.opprettJournalpost(
                    dokumentInnhold = dokument,
                    søknadId = søknadId,
                    callId = "123",
                )
            }
        }
    }

    @Test
    fun `joark svarer med null i ferdigstilt`() {
        val mock = MockEngine {
            respond(
                content = svarNullIFerdigstiltJoark,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

//        val joarkResonse = JoarkClient.JoarkResponse(
//            journalpostId = journalpostId,
//            journalpostferdigstilt = null,
//            dokumenter = listOf(dokumentResponse),
//        )

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
                søknadId = søknadId,
                callId = "123",
            )

            resp shouldBe journalpostId
        }
    }

    @Test
    fun `joark svarer uten journalpostid`() {
        val mock = MockEngine {
            respond(
                content = svarUtenJournalpostId,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val joarkResonse = JoarkClient.JoarkResponse(
            journalpostId = null,
            journalpostferdigstilt = null,
            dokumenter = listOf(dokumentResponse),
        )

        val client = httpClientGeneric(mock)
        val joarkClient = JoarkClient(
            client = client,
            baseUrl = baseurl,
        ) { getMockToken() }

        runTest {
            shouldThrow<IllegalStateException> {
                joarkClient.opprettJournalpost(
                    dokumentInnhold = dokument,
                    søknadId = søknadId,
                    callId = "123",
                )
            }.message shouldBe "Fikk 201 Created fra Joark, men vi fikk ingen journalpostId. response=JoarkResponse(journalpostId=null, journalpostferdigstilt=null, dokumenter=[Dokumenter(dokumentInfoId=485227498, tittel=Søknad om tiltakspenger)])"
        }
    }

    private suspend fun getMockToken(): AccessToken {
        return AccessToken("token", Instant.now().plusSeconds(3600)) {}
    }

    private val dokument = Journalpost.Søknadspost.from(
        fnr = "ident",
        søknad = søknad(),
        pdf = "dette er pdf innholdet".toByteArray(),
        vedlegg = listOf(
            Vedlegg(
                filnavn = "filnavnVedlegg",
                contentType = "application/pdf",
                dokument = "vedleggInnhold".toByteArray(),
            ),
        ),
    )

    private val dokumentInfoId = "485227498"
    private val dokumentInfoVedleggId = "485227499"
    private val dokumentTittel = "Søknad om tiltakspenger"
    private val dokumentVedleggFilnavn = "filnavnVedlegg"
    private val dokumentResponse = JoarkClient.Dokumenter(
        dokumentInfoId = dokumentInfoId,
        tittel = dokumentTittel,

    )
    private val okSvarJoark = """
        {
          "journalpostId": "$journalpostId",
          "journalpostferdigstilt": true,
          "dokumenter": [
            {
              "dokumentInfoId": "$dokumentInfoId",
              "tittel": "$dokumentTittel"
            },
            {
              "dokumentInfoId": "$dokumentInfoVedleggId",
              "tittel": "$dokumentVedleggFilnavn"
            }
          ]
        }
    """.trimIndent()

    private val svarIkkeFerdigstiltJoark = """
        {
          "journalpostId": "$journalpostId",
          "journalpostferdigstilt": false,
          "dokumenter": [
            {
              "dokumentInfoId": "$dokumentInfoId",
              "tittel": "$dokumentTittel"
            }
          ]
        }
    """.trimIndent()

    private val svarNullIFerdigstiltJoark = """
        {
          "journalpostId": "$journalpostId",
          "journalpostferdigstilt": null,
          "dokumenter": [
            {
              "dokumentInfoId": "$dokumentInfoId",
              "tittel": "$dokumentTittel"
            }
          ]
        }
    """.trimIndent()

    private val svarUtenJournalpostId = """
        {
          "journalpostId": null,
          "journalpostferdigstilt": null,
          "dokumenter": [
            {
              "dokumentInfoId": "$dokumentInfoId",
              "tittel": "$dokumentTittel"
            }
          ]
        }
    """.trimIndent()
}
