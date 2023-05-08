package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentDisposition
import io.ktor.http.HeaderValueParam
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.utils.io.core.Input
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService
import no.nav.tiltakspenger.soknad.api.util.Detect
import no.nav.tiltakspenger.soknad.api.util.sjekkContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SøknadServiceTest {
    private val mockPdfService = mockk<PdfService>().also { mock ->
        coEvery { mock.lagPdf(any()) }
    }
    private val mockJoarkService = mockk<JoarkService>().also { mock ->
        coEvery { mock.sendPdfTilJoark(any(), any(), any(), any()) }
    }

    private val gyldigSøknad = """
        {
          "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "arrangør": "test",
            "type": "test",
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "barnetillegg": {
            "manueltRegistrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "etternavn": "Test",
                "fødselsdato": "2025-01-01",
                "bostedsland": "Test"
              }
            ],
            "søkerOmBarnetillegg": true,
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2025-01-01",
                "etternavn": "Testesen"
              }
            ],
            "ønskerÅSøkeBarnetilleggForAndreBarn": true
          },
          "etterlønn": {
            "mottarEllerSøktEtterlønn": true,
            "utbetaler": "Test",
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "institusjonsopphold": {
            "borPåInstitusjon": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "introduksjonsprogram": {
            "deltar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "kvalifiseringsprogram": {
            "deltar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "pensjonsordning": {
            "utbetaler": "Test",
            "mottarEllerSøktPensjonsordning": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "harBekreftetAlleOpplysninger": true
        }
    """.trimMargin()

    class MockMultiPartData(private val partDataList: MutableList<PartData>) : MultiPartData {
        override suspend fun readPart(): PartData? {
            return if (partDataList.isNotEmpty()) partDataList.removeLast() else null
        }
    }

    private val søknadService = SøknadServiceImpl(
        pdfService = mockPdfService,
        joarkService = mockJoarkService,
    )

    @Test
    fun `taInnSøknadSomMultipart leser inn MultiPartData med gyldig søknad to vedlegg`() {
        val input: Input = mockk()
        every { input.endOfInput } returns true
        justRun { input.release() }
        mockkStatic("no.nav.tiltakspenger.soknad.api.util.DetectKt")
        every { sjekkContentType(any()) } returns Detect.APPLICATON_PDF

        val mockMultiPartData = MockMultiPartData(
            mutableListOf(
                PartData.FormItem(
                    gyldigSøknad,
                    {},
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, ContentDisposition("søknad", listOf(HeaderValueParam("name", "søknad"))))
                    },
                ),
                PartData.FileItem(
                    { input },
                    { input.release() },
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, ContentDisposition("vedlegg", listOf(HeaderValueParam("name", "vedlegg"))))
                    },
                ),
                PartData.FileItem(
                    { input },
                    { input.release() },
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, ContentDisposition("vedlegg", listOf(HeaderValueParam("name", "vedlegg"))))
                    },
                ),
            ),
        )

        runBlocking {
            val (søknad, vedlegg) = søknadService.taInnSøknadSomMultipart(mockMultiPartData)
            assertEquals(søknad.tiltak.aktivitetId, "123")
            assertEquals(vedlegg.size, 2)
        }
    }
}
