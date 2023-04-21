package no.nav.tiltakspenger.soknad.api.joark

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.config.ApplicationConfig
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.soknad.api.domain.Barnetillegg
import no.nav.tiltakspenger.soknad.api.domain.Etterlønn
import no.nav.tiltakspenger.soknad.api.domain.Institusjonsopphold
import no.nav.tiltakspenger.soknad.api.domain.Introduksjonsprogram
import no.nav.tiltakspenger.soknad.api.domain.Kvalifiseringsprogram
import no.nav.tiltakspenger.soknad.api.domain.Pensjonsordning
import no.nav.tiltakspenger.soknad.api.domain.Periode
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.domain.Tiltak
import no.nav.tiltakspenger.soknad.api.httpClientGeneric
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class JoarkClientTest {
    private val journalpostId = "1"

    @OptIn(ExperimentalCoroutinesApi::class)
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
        val mockTokenService = mockk<TokenService>()
        coEvery { mockTokenService.getToken(any()) } returns "token"
        val config = ApplicationConfig("application.test.conf")
        val joarkClient = JoarkClient(
            config = config,
            client = client,
            tokenService = mockTokenService,

        )

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
            )

            resp shouldBe journalpostId
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joark svarer ikke ferdigstilt`() {
        val mock = MockEngine {
            respond(
                content = svarIkkeFerdigstiltJoark,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val mockTokenService = mockk<TokenService>()
        coEvery { mockTokenService.getToken(any()) } returns "token"

        val joarkResonse = JoarkClient.JoarkResponse(
            journalpostId = journalpostId,
            journalpostferdigstilt = false,
            dokumenter = listOf(dokumentResponse),
        )

        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val joarkClient = JoarkClient(
            config = config,
            client = client,
            tokenService = mockTokenService,
        )

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
            )

            resp shouldBe journalpostId
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joark svarer med feil`() {
        val mock = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val mockTokenService = mockk<TokenService>()
        coEvery { mockTokenService.getToken(any()) } returns "token"

        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val joarkClient = JoarkClient(
            config = config,
            client = client,
            tokenService = mockTokenService,
        )

        runTest {
            shouldThrow<RuntimeException> {
                joarkClient.opprettJournalpost(
                    dokumentInnhold = dokument,
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joark svarer med null i ferdigstilt`() {
        val mock = MockEngine {
            respond(
                content = svarNullIFerdigstiltJoark,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val mockTokenService = mockk<TokenService>()
        coEvery { mockTokenService.getToken(any()) } returns "token"

        val joarkResonse = JoarkClient.JoarkResponse(
            journalpostId = journalpostId,
            journalpostferdigstilt = null,
            dokumenter = listOf(dokumentResponse),
        )

        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val joarkClient = JoarkClient(
            config = config,
            client = client,
            tokenService = mockTokenService,
        )

        runTest {
            val resp = joarkClient.opprettJournalpost(
                dokumentInnhold = dokument,
            )

            resp shouldBe journalpostId
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `joark svarer uten journalpostid`() {
        val mock = MockEngine {
            respond(
                content = svarUtenJournalpostId,
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val mockTokenService = mockk<TokenService>()
        coEvery { mockTokenService.getToken(any()) } returns "token"

        val joarkResonse = JoarkClient.JoarkResponse(
            journalpostId = null,
            journalpostferdigstilt = null,
            dokumenter = listOf(dokumentResponse),
        )

        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val joarkClient = JoarkClient(
            config = config,
            client = client,
            tokenService = mockTokenService,
        )

        runTest {
            shouldThrow<IllegalStateException> {
                joarkClient.opprettJournalpost(
                    dokumentInnhold = dokument,
                )
            }.message shouldBe "Kallet til Joark gikk ok, men vi fikk ingen journalpostId fra Joark. response=$joarkResonse"
        }
    }

    private val dokument = Journalpost.Søknadspost.from(
        fnr = "ident",
        søknadDTO = SøknadDTO(
            kvalifiseringsprogram = Kvalifiseringsprogram(
                deltar = false,
                periode = null,
            ),
            introduksjonsprogram = Introduksjonsprogram(
                deltar = false,
                periode = null,
            ),
            institusjonsopphold = Institusjonsopphold(
                borPåInstitusjon = false,
                periode = null,
            ),
            tiltak = Tiltak(
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 1),
                ),
                aktivitetId = "123",
                søkerHeleTiltaksperioden = false,
            ),
            etterlønn = Etterlønn(
                mottarEllerSøktEtterlønn = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 1),
                ),
                utbetaler = "test",
            ),
            pensjonsordning = Pensjonsordning(
                mottarEllerSøktPensjonsordning = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 1),
                ),
                utbetaler = "test",
            ),
            barnetillegg = Barnetillegg(
                søkerOmBarnetillegg = false,
                ønskerÅSøkeBarnetilleggForAndreBarn = null,
                manueltRegistrerteBarnSøktBarnetilleggFor = emptyList(),
                registrerteBarnSøktBarnetilleggFor = emptyList(),
            ),
        ),
        pdf = "dette er pdf innholdet".toByteArray(),
    )

    private val dokumentInfoId = "485227498"
    private val dokumentTittel = "Søknad om tiltakspenger"
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
