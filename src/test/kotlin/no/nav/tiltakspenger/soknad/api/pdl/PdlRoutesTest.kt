package no.nav.tiltakspenger.soknad.api.pdl

import com.nimbusds.jwt.SignedJWT
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.tiltakspenger.soknad.api.configureTestApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class PdlRoutesTest {
    private val mockOAuth2Server = MockOAuth2Server()

    @BeforeAll
    fun setup() = mockOAuth2Server.start(8080)

    @AfterAll
    fun after() = mockOAuth2Server.shutdown()

    private val mockedPdlService = mockk<PdlService>().also { mock ->
        coEvery { mock.hentPersonaliaMedBarn(any(), any()) } returns Person(
            fornavn = "foo",
            etternavn = "bar",
            mellomnavn = "baz",
            adressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT,
        ).toPersonDTO()
    }

    private fun issueTestToken(): SignedJWT {
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
    fun `get på personalia-endepunkt skal svare med personalia fra PDLService hvis tokenet er gyldig og validerer ok`() {
        val token = issueTestToken()
        testApplication {
            configureTestApplication(pdlService = mockedPdlService)
            runBlocking {
                val response = client.get("/personalia") {
                    contentType(type = ContentType.Application.Json)
                    header("Authorization", "Bearer ${token.serialize()}")
                }
                Assertions.assertEquals(HttpStatusCode.OK, response.status)
            }
        }
    }
}
