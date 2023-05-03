package no.nav.tiltakspenger.soknad.api.soknad

import com.nimbusds.jwt.SignedJWT
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.configureTestApplication
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class SøknadRoutesTest {
    private val pdlServiceMock = mockk<PdlService>().also { mock ->
        coEvery { mock.hentPersonaliaMedBarn(any(), any()) } returns PersonDTO(
            fornavn = "fornavn",
            mellomnavn = null,
            etternavn = "etternavn",
            barn = emptyList(),
        )
    }
    private val avServiceMock = mockk<AvService>().also { mock ->
        coEvery { mock.gjørVirussjekkAvVedlegg(any()) } returns Unit
    }

    private val mockOAuth2Server = MockOAuth2Server()

    @BeforeAll
    fun setup() = mockOAuth2Server.start(8080)

    @AfterAll
    fun after() = mockOAuth2Server.shutdown()

    fun issueTestToken(): SignedJWT {
        return mockOAuth2Server.issueToken(
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
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 400 ved ugyldig søknad`() {
        val søknadServiceMock = mockk<SøknadService>().also { mock ->
            coEvery { mock.taInnSøknadSomMultipart(any()) } throwsMany listOf(BadRequestException("1"), UnrecognizedFormItemException("2"), MissingContentException("3"), mockk<CannotTransformContentToTypeException>(), UninitializedPropertyAccessException("4"))
        }

        val token = issueTestToken()

        testApplication {
            configureTestApplication(søknadService = søknadServiceMock)
            kotlin.runCatching {
                val response = client.post("/soknad") {
                    header("Authorization", "Bearer ${token.serialize()}")
                    setBody(
                        MultiPartFormDataContent(
                            formData {},
                            "WebAppBoundary",
                            ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                        ),
                    )
                }
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 204 No Content ved gyldig søknad `() {
        val søknadServiceMock = mockk<SøknadService>().also { mock ->
            coEvery { mock.taInnSøknadSomMultipart(any()) } returns Pair(mockk(), emptyList())
            coEvery { mock.opprettDokumenterOgArkiverIJoark(any(), any(), any(), any(), any()) } returns "123"
        }

        val token = issueTestToken()

        testApplication {
            configureTestApplication(søknadService = søknadServiceMock, avService = avServiceMock, pdlService = pdlServiceMock)
            val response = client.post("/soknad") {
                header("Authorization", "Bearer ${token.serialize()}")
                setBody(
                    MultiPartFormDataContent(
                        formData {},
                        "WebAppBoundary",
                        ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                    ),
                )
            }
            assertEquals(HttpStatusCode.Created, response.status)
        }
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 500 hvis journalføringen feiler`() {
        val søknadServiceMock = mockk<SøknadService>().also { mock ->
            coEvery { mock.taInnSøknadSomMultipart(any()) } returns Pair(mockk(), emptyList())
            coEvery { mock.opprettDokumenterOgArkiverIJoark(any(), any(), any(), any(), any()) } throws IllegalStateException("blabla")
        }

        val token = issueTestToken()

        testApplication {
            configureTestApplication(søknadService = søknadServiceMock, avService = avServiceMock, pdlService = pdlServiceMock)
            kotlin.runCatching {
                val response = client.post("/soknad") {
                    header("Authorization", "Bearer ${token.serialize()}")
                    setBody(
                        MultiPartFormDataContent(
                            formData {},
                            "WebAppBoundary",
                            ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                        ),
                    )
                }
                assertEquals(HttpStatusCode.InternalServerError, response.status)
            }
        }
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 500 hvis man ikke får hentet personalia fra PDL`() {
        val søknadServiceMock = mockk<SøknadService>().also { mock ->
            coEvery { mock.taInnSøknadSomMultipart(any()) } returns Pair(mockk(), emptyList())
            coEvery { mock.opprettDokumenterOgArkiverIJoark(any(), any(), any(), any(), any()) } returns "123"
        }

        val pdlServiceMock = mockk<PdlService>().also { mock ->
            coEvery { mock.hentPersonaliaMedBarn(any(), any()) } throws IllegalStateException("blabla")
        }

        val token = issueTestToken()

        testApplication {
            configureTestApplication(søknadService = søknadServiceMock, avService = avServiceMock, pdlService = pdlServiceMock)
            kotlin.runCatching {
                val response = client.post("/soknad") {
                    header("Authorization", "Bearer ${token.serialize()}")
                    setBody(
                        MultiPartFormDataContent(
                            formData {},
                            "WebAppBoundary",
                            ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary"),
                        ),
                    )
                }
                assertEquals(HttpStatusCode.InternalServerError, response.status)
            }
        }
    }
}
