package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class Gjenlevendepensjon {
    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `Gjenlevendepensjon periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(gjenlevendepensjon = fraDatoEtterTil))
            .valider() shouldContain "Gjenlevendepensjon fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `Gjenlevendepensjon med mottar = false skal ikke ha en periode`() {
        val periodeMedMottarFalse = """
            "gjenlevendepensjon": {
                "mottar": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(gjenlevendepensjon = periodeMedMottarFalse))
            .valider() shouldContain "Gjenlevendepensjon med mottar = false kan ikke ha noen periode"
    }

    @Test
    fun `Gjenlevendepensjon periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": {
                  "fra": "2024-01-01",
                  "til": "2025-04-01"
                }
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
            søknad(tiltak = tiltak, gjenlevendepensjon = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `Gjenlevendepensjon periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": {
                  "fra": "2025-01-01",
                  "til": "2026-04-01"
                }
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
            søknad(tiltak = tiltak, gjenlevendepensjon = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `Gjenlevendepensjon periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": {
                  "fra": "2024-01-01",
                  "til": "2024-05-01"
                }
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
            søknad(tiltak = tiltak, gjenlevendepensjon = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Gjenlevendepensjon periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `Gjenlevendepensjon periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": {
                  "fra": "2026-01-01",
                  "til": "2026-05-01"
                }
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
            søknad(tiltak = tiltak, gjenlevendepensjon = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Gjenlevendepensjon periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `Gjenlevendepensjon med mottar = true må ha en periode`() {
        val mottarTrueUtenPeriode = """
            "gjenlevendepensjon": {
                "mottar": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(gjenlevendepensjon = mottarTrueUtenPeriode))
            .valider() shouldContain "Gjenlevendepensjon med mottar = true må ha periode"
    }
}
