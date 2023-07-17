package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.ContentDisposition
import io.ktor.http.HeaderValueParam
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.utils.io.core.Input
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService
import no.nav.tiltakspenger.soknad.api.soknad.validering.søknad
import no.nav.tiltakspenger.soknad.api.util.Detect
import no.nav.tiltakspenger.soknad.api.util.sjekkContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SøknadServiceTest {
    private val mockPdfService = mockk<PdfService>().also { mock ->
        coEvery { mock.lagPdf(any()) }
    }
    private val mockJoarkService = mockk<JoarkService>().also { mock ->
        coEvery { mock.sendPdfTilJoark(any(), any(), any(), any(), any()) }
    }

    private val gyldigSøknad = """
        {
          "tiltak": {
            "aktivitetId": "123",
            "arrangør": "test",
            "type": "test",
            "typeNavn": "test",
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
                "fødselsdato": "2023-01-01",
                "oppholdInnenforEøs": true
              }
            ],
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2025-01-01",
                "etternavn": "Testesen",
                "oppholdInnenforEøs": true
              }
            ]
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
          "sykepenger": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "gjenlevendepensjon": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "alderspensjon": {
            "mottar": true,
            "fraDato": "2025-01-01"
          },
          "supplerendestønadover67": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          },
          "supplerendestønadflyktninger": {
            "mottar": true,
            "periode": {
               "fra": "2025-01-01",
               "til": "2025-01-01"
            }
          },   
          "jobbsjansen": {
             "mottar": true,
             "periode": {
                "fra": "2025-01-01",
                "til": "2025-01-01"
             }
          },
          "etterlønn": {
            "mottar": true
          },
          "lønnetArbeid": {
            "erILønnetArbeid": true
          },
          "pensjonsordning": {
            "mottar": true,
            "periode": {
                "fra": "2025-01-01",
                "til": "2025-01-01"
             }
          },
          "mottarAndreUtbetalinger": true,
          "harBekreftetAlleOpplysninger": true,
          "harBekreftetÅSvareSåGodtManKan": true
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
                        append(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition("søknad", listOf(HeaderValueParam("name", "søknad"))),
                        )
                    },
                ),
                PartData.FileItem(
                    { input },
                    { input.release() },
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition("vedlegg", listOf(HeaderValueParam("name", "vedlegg"))),
                        )
                    },
                ),
                PartData.FileItem(
                    { input },
                    { input.release() },
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition("vedlegg", listOf(HeaderValueParam("name", "vedlegg"))),
                        )
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

    @Test
    fun `taInnSøknadSomMultipart gir feil ved ugyldig søknad`() {
        val input: Input = mockk()
        every { input.endOfInput } returns true
        justRun { input.release() }
        mockkStatic("no.nav.tiltakspenger.soknad.api.util.DetectKt")
        every { sjekkContentType(any()) } returns Detect.APPLICATON_PDF

        val introduksjonsprogram = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        val mockMultiPartData = MockMultiPartData(
            mutableListOf(
                PartData.FormItem(
                    søknad(introduksjonsprogram = introduksjonsprogram),
                    {},
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition("søknad", listOf(HeaderValueParam("name", "søknad"))),
                        )
                    },
                ),
            ),
        )

        runBlocking {
            assertThrows<RequestValidationException> { søknadService.taInnSøknadSomMultipart(mockMultiPartData) }
        }
    }

    @Test
    fun `taInnSøknadSomMultipart escaper potensiell XSS`() {
        val mockedTiltak = """
            "tiltak": {
                "aktivitetId": "<script>blabla</script>",
                "periode": {
                  "fra": "2025-01-01",
                  "til": "2025-01-01"
                },
                "arenaRegistrertPeriode": {
                  "fra": "2025-01-01",
                  "til": "2025-01-01"
                },
                "arrangør": "<script>arrangør</script>",
                "typeNavn": "<script>typeNavn</script>",
                "type": "<script>type</script>"
            }
        """.trimIndent()

        val mockedBarnetillegg = """
            "barnetillegg": {
                "manueltRegistrerteBarnSøktBarnetilleggFor": [
                  {
                    "fornavn": "<script>a",
                    "mellomnavn": "<script>b",
                    "etternavn": "<script>c",
                    "fødselsdato": "2023-01-01",
                    "oppholdInnenforEøs": true
                  }
                ],
                "registrerteBarnSøktBarnetilleggFor": [
                  {
                    "fornavn": "<script>a",
                    "mellomnavn": "<script>b",
                    "fødselsdato": "2025-01-01",
                    "etternavn": "<script>c",
                    "oppholdInnenforEøs": true
                  }
                ]
            }
        """.trimIndent()

        val mockMultiPartData = MockMultiPartData(
            mutableListOf(
                PartData.FormItem(
                    søknad(tiltak = mockedTiltak, barnetillegg = mockedBarnetillegg),
                    {},
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition("søknad", listOf(HeaderValueParam("name", "søknad"))),
                        )
                    },
                ),
            ),
        )

        runBlocking {
            val (spørsmålsbesvarelser) = søknadService.taInnSøknadSomMultipart(mockMultiPartData)
            assertEquals(spørsmålsbesvarelser.tiltak.arrangør, "&lt;script&gt;arrangør&lt;/script&gt;")
            assertEquals(spørsmålsbesvarelser.tiltak.type, "&lt;script&gt;type&lt;/script&gt;")
            assertEquals(spørsmålsbesvarelser.tiltak.typeNavn, "&lt;script&gt;typeNavn&lt;/script&gt;")

            val manueltRegistrertBarn = spørsmålsbesvarelser.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.get(0)
            assertEquals(manueltRegistrertBarn.fornavn, "&lt;script&gt;a")
            assertEquals(manueltRegistrertBarn.mellomnavn, "&lt;script&gt;b")
            assertEquals(manueltRegistrertBarn.etternavn, "&lt;script&gt;c")

            val registrertBarn = spørsmålsbesvarelser.barnetillegg.registrerteBarnSøktBarnetilleggFor.get(0)
            assertEquals(registrertBarn.fornavn, "&lt;script&gt;a")
            assertEquals(registrertBarn.mellomnavn, "&lt;script&gt;b")
            assertEquals(registrertBarn.etternavn, "&lt;script&gt;c")
        }
    }
}
