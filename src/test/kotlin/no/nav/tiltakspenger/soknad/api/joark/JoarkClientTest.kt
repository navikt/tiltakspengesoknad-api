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
import no.nav.tiltakspenger.soknad.api.domain.Personopplysninger
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.httpClientGeneric
import no.nav.tiltakspenger.soknad.api.soknad.Alderspensjon
import no.nav.tiltakspenger.soknad.api.soknad.Barnetillegg
import no.nav.tiltakspenger.soknad.api.soknad.Etterlønn
import no.nav.tiltakspenger.soknad.api.soknad.Gjenlevendepensjon
import no.nav.tiltakspenger.soknad.api.soknad.Institusjonsopphold
import no.nav.tiltakspenger.soknad.api.soknad.Introduksjonsprogram
import no.nav.tiltakspenger.soknad.api.soknad.Jobbsjansen
import no.nav.tiltakspenger.soknad.api.soknad.Kvalifiseringsprogram
import no.nav.tiltakspenger.soknad.api.soknad.Pensjonsordning
import no.nav.tiltakspenger.soknad.api.soknad.Periode
import no.nav.tiltakspenger.soknad.api.soknad.Supplerendestønadflyktninger
import no.nav.tiltakspenger.soknad.api.soknad.Supplerendestønadover67
import no.nav.tiltakspenger.soknad.api.soknad.Sykepenger
import no.nav.tiltakspenger.soknad.api.soknad.Tiltak
import no.nav.tiltakspenger.soknad.api.tiltak.Deltakelsesperiode
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

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

//        val joarkResonse = JoarkClient.JoarkResponse(
//            journalpostId = journalpostId,
//            journalpostferdigstilt = false,
//            dokumenter = listOf(dokumentResponse),
//        )

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

//        val joarkResonse = JoarkClient.JoarkResponse(
//            journalpostId = journalpostId,
//            journalpostferdigstilt = null,
//            dokumenter = listOf(dokumentResponse),
//        )

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
            acr = "Level4",
            personopplysninger = Personopplysninger(
                ident = "12345678901",
                fornavn = "fornavn",
                etternavn = "etternavn",
            ),
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
                arrangør = "test",
                type = "test",
                typeNavn = "test",
                arenaRegistrertPeriode = Deltakelsesperiode(
                    fra = LocalDate.MAX,
                    til = LocalDate.MAX,
                ),
            ),
            mottarAndreUtbetalinger = false,
            sykepenger = Sykepenger(
                mottar = true,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 31),
                ),
            ),
            gjenlevendepensjon = Gjenlevendepensjon(
                mottar = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 31),
                ),
            ),
            alderspensjon = Alderspensjon(
                mottar = false,
                fraDato = LocalDate.of(2023, 1, 1),
            ),
            supplerendestønadover67 = Supplerendestønadover67(
                mottar = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 31),
                ),
            ),
            supplerendestønadflyktninger = Supplerendestønadflyktninger(
                mottar = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 31),
                ),
            ),
            pensjonsordning = Pensjonsordning(
                mottar = false,
            ),
            etterlønn = Etterlønn(
                mottar = false,
            ),
            jobbsjansen = Jobbsjansen(
                mottar = false,
                periode = Periode(
                    fra = LocalDate.of(2023, 1, 1),
                    til = LocalDate.of(2023, 1, 31),
                ),
            ),
            barnetillegg = Barnetillegg(
                manueltRegistrerteBarnSøktBarnetilleggFor = emptyList(),
                registrerteBarnSøktBarnetilleggFor = emptyList(),
            ),
            innsendingTidspunkt = LocalDateTime.now(),
            harBekreftetAlleOpplysninger = true,
            harBekreftetÅSvareSåGodtManKan = true,
            vedleggsnavn = emptyList(),
        ),
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
