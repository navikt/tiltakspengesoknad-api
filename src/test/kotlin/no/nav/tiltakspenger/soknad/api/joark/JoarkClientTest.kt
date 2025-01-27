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
    fun `opprettJournalpost - skal ikke ferdigstille, blir opprettet - returnerer journalpostid`() {
        val mock = MockEngine {
            respond(
                content = svarIkkeFerdigstiltJoark,
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
                request = journalpostRequest(),
                søknadId = søknadId,
                callId = "123",
            )

            resp shouldBe journalpostId
        }
    }

    @Test
    fun `opprettJournalpost - skal ferdigstille, blir opprettet og ferdigstilt - returnerer journalpostid`() {
        val mock = MockEngine {
            respond(
                content = svarFerdigstiltJoark,
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
                request = journalpostRequest("1234", "saksnummer"),
                søknadId = søknadId,
                callId = "123",
            )

            resp shouldBe journalpostId
        }
    }

    @Test
    fun `opprettJournalpost - skal ferdigstille, blir opprettet men ikke ferdigstilt - kaster feil`() {
        val mock = MockEngine {
            respond(
                content = svarIkkeFerdigstiltJoark,
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
            shouldThrow<RuntimeException> {
                joarkClient.opprettJournalpost(
                    request = journalpostRequest("1234", "saksnummer"),
                    søknadId = søknadId,
                    callId = "123",
                )
            }
        }
    }

    @Test
    fun `hvis joark svarer med 409 Conflict returnerer opprettJournalpost en journalpostId hvis vi har fått en`() {
        val mock = MockEngine {
            respond(
                content = svarIkkeFerdigstiltJoark,
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
                request = journalpostRequest(),
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
                    request = journalpostRequest(),
                    søknadId = søknadId,
                    callId = "123",
                )
            }
        }
    }

    private fun getMockToken(): AccessToken {
        return AccessToken("token", Instant.now().plusSeconds(3600)) {}
    }

    private fun journalpostRequest(
        journalforendeEnhet: String? = null,
        saksnummer: String? = null,
    ) = JournalpostRequest.from(
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
        journalforendeEnhet = journalforendeEnhet,
        saksnummer = saksnummer,
    )

    private val svarFerdigstiltJoark = """
        {
          "journalpostId": "$journalpostId",
          "journalpostferdigstilt": true
        }
    """.trimIndent()

    private val svarIkkeFerdigstiltJoark = """
        {
          "journalpostId": "$journalpostId",
          "journalpostferdigstilt": false
        }
    """.trimIndent()
}
