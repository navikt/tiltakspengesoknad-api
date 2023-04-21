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
import no.nav.tiltakspenger.soknad.api.domain.Barnetillegg
import no.nav.tiltakspenger.soknad.api.domain.Etterlønn
import no.nav.tiltakspenger.soknad.api.domain.Institusjonsopphold
import no.nav.tiltakspenger.soknad.api.domain.Introduksjonsprogram
import no.nav.tiltakspenger.soknad.api.domain.Kvalifiseringsprogram
import no.nav.tiltakspenger.soknad.api.domain.Pensjonsordning
import no.nav.tiltakspenger.soknad.api.domain.Periode
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.domain.Tiltak
import no.nav.tiltakspenger.soknad.api.httpClientGeneric
import org.junit.jupiter.api.Test
import java.time.LocalDate

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

    private fun tomSøknad() = Søknad(
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
        ),
        etterlønn = Etterlønn(
            mottarEllerSøktEtterlønn = false,
            periode = Periode(
                fra = LocalDate.of(2023, 1, 1),
                til = LocalDate.of(2023, 1, 31),
            ),
            utbetaler = "test",
        ),
        pensjonsordning = Pensjonsordning(
            mottarEllerSøktPensjonsordning = false,
            periode = Periode(
                fra = LocalDate.of(2023, 1, 1),
                til = LocalDate.of(2023, 1, 31),
            ),
            utbetaler = "test",
        ),
        barnetillegg = Barnetillegg(
            søkerOmBarnetillegg = false,
            ønskerÅSøkeBarnetilleggForAndreBarn = null,
            manueltRegistrerteBarnSøktBarnetilleggFor = emptyList(),
            registrerteBarnSøktBarnetilleggFor = emptyList(),
        ),
    )
}
