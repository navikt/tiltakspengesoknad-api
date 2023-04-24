package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.tiltakspenger.soknad.api.configureTestApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class SøknadRoutesTest {
    val ugyldigSøknad = """{}"""
    val gyldigSøknad = """
        {
          "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
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
          }
        }
    """.trimMargin()

    private val søknadServiceMock = mockk<SøknadService>().also { mock ->
        coEvery { mock.opprettDokumenterOgArkiverIJoark(any(), any(), any()) } returns "1"
    }

    private val mockOAuth2Server = MockOAuth2Server()

    @BeforeAll
    fun setup() = mockOAuth2Server.start(8080)

    @AfterAll
    fun after() = mockOAuth2Server.shutdown()

    @Test
    fun `post på soknad-endepunkt skal svare med 400 ved ugyldig søknad`() {
        val token = mockOAuth2Server.issueToken(
            "tokendings",
            "testClientId",
            DefaultOAuth2TokenCallback(
                audience = listOf("audience"),
                claims = mapOf(
                    "acr" to "Level4",
                    "pid" to "123",
                ),
            ),
        )

        testApplication {
            configureTestApplication()
            val response = client.post("/soknad") {
                header("Authorization", "Bearer ${token.serialize()}")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("søknad", ugyldigSøknad)
                            append("vedlegg", "")
                        },
                        "WebAppBoundary",
                        ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                    ),
                )
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 204 No Content ved gyldig søknad `() {
        val token = mockOAuth2Server.issueToken(
            "tokendings",
            "testClientId",
            DefaultOAuth2TokenCallback(
                audience = listOf("audience"),
                claims = mapOf(
                    "acr" to "Level4",
                    "pid" to "123",
                ),
            ),
        )

        testApplication {
            configureTestApplication(søknadService = søknadServiceMock)
            val response = client.post("/soknad") {
                header("Authorization", "Bearer ${token.serialize()}")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("søknad", gyldigSøknad)
                            append("vedlegg", "")
                        },
                        "WebAppBoundary",
                        ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                    ),
                )
            }
            assertEquals(HttpStatusCode.Created, response.status)
        }
    }
}
