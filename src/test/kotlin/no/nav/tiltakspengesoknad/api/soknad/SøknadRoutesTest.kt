package no.nav.tiltakspengesoknad.api.soknad

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import no.nav.tiltakspengesoknad.api.installJacksonFeature
import no.nav.tiltakspengesoknad.api.setupRouting
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SøknadRoutesTest {
    val ugyldigSøknad = """{}"""
    val gyldigSøknad = """
        {
            "deltarIKvp": false,
            "deltarIIntroprogrammet": false,
            "borPåInstitusjon": false,
            "tiltak": {
                "type": "foo",
                "periode": {
                    "fra": "2025-01-01",
                    "til": "2025-01-02"
                },
                "antallDagerIUken": 5
            },
            "mottarEllerSøktPensjonsordning": false,
            "mottarEllerSøktEtterlønn": false,
            "søkerOmBarnetillegg": false
        }
    """.trimMargin()

    @Test
    fun `post på soknad-endepunkt skal svare med 400 ved ugyldig søknad`() {
        testApplication {
            application {
                setupRouting()
                installJacksonFeature()
            }
            val response = client.post("/soknad") {
                contentType(type = ContentType.Application.Json)
                setBody(ugyldigSøknad)
            }
            assertEquals(response.status, HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `post på soknad-endepunkt skal svare med 204 No Content ved gyldig søknad `() {
        testApplication {
            application {
                setupRouting()
                installJacksonFeature()
            }
            val response = client.post("/soknad") {
                contentType(type = ContentType.Application.Json)
                setBody(gyldigSøknad)
            }
            assertEquals(response.status, HttpStatusCode.NoContent)
        }
    }
}
