package no.nav.tiltakspenger.soknad.api.pdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.config.ApplicationConfig
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
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class PdfClientTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `får tilbake en pdf hvis alt går ok`() {
        val pdf = "dette er innholdet i pdf vi får tilbake fra pdfGen".toByteArray()
        val mock = MockEngine {
            respond(
                content = pdf,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val pdfClient = PdfClient(
            config = config,
            client = client,
        )

        runTest {
            val resp = pdfClient.genererPdf(tomSøknad())

            resp shouldBe pdf
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `kaster en feil hvis generering av pdf ikke går ok`() {
        val pdf = "".toByteArray()
        val mock = MockEngine {
            respond(
                content = pdf,
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = httpClientGeneric(mock)
        val config = ApplicationConfig("application.test.conf")
        val pdfClient = PdfClient(
            config = config,
            client = client,
        )

        runTest {
            shouldThrow<RuntimeException> {
                pdfClient.genererPdf(tomSøknad())
            }
        }
    }

    private fun tomSøknad() = SøknadDTO(
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
                til = LocalDate.of(2023, 1, 31),
            ),
            aktivitetId = "123",
            søkerHeleTiltaksperioden = false,
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
            mottar = false,
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
            søkerOmBarnetillegg = false,
            ønskerÅSøkeBarnetilleggForAndreBarn = null,
            manueltRegistrerteBarnSøktBarnetilleggFor = emptyList(),
            registrerteBarnSøktBarnetilleggFor = emptyList(),
        ),
        harBekreftetAlleOpplysninger = true,
        harBekreftetÅSvareSåGodtManKan = true,
        innsendingTidspunkt = LocalDateTime.now(),
        vedleggsnavn = emptyList(),
    )
}
