package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class Alderspensjon {
    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `Alderspensjon med mottar = false skal ikke ha en fra dato`() {
        val periodeMedMottarFalse = """
            "alderspensjon": {
                "mottar": false,
                "fraDato": "2025-01-01"
            }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(alderspensjon = periodeMedMottarFalse))
            .valider() shouldContain "Alderspensjon med mottar = false kan ikke ha noen fra dato"
    }

    @Test
    fun `Alderspensjon fra dato kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "alderspensjon": {
                "mottar": true,
                "fraDato": "2025-01-01"
            }
        """.trimIndent()

        val tiltak = """
        "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-04-01"
            },
            "arrangør": "test",
            "type": "test",
            "typeNavn": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, alderspensjon = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `Alderspensjon fra dato kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "alderspensjon": {
                "mottar": true,
                "fraDato": "2026-05-01"
            }
        """.trimIndent()

        val tiltak = """
        "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-04-01"
            },
            "arrangør": "test",
            "type": "test",
            "typeNavn": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, alderspensjon = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Alderspensjon fra dato kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `Alderspensjon med mottar = true må ha en fra dato`() {
        val mottarTrueUtenPeriode = """
            "alderspensjon": {
                "mottar": true,
                "fraDato": null
            }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(alderspensjon = mottarTrueUtenPeriode))
            .valider() shouldContain "Alderspensjon med mottar = true må ha fra dato"
    }
}
