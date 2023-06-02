package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class SupplerendeStønadUføreFlyktninger {
    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "supplerendestønadflyktninger": {
                "mottar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(supplerendestønadflyktninger = fraDatoEtterTil))
            .valider() shouldContain "SupplerendeStønadUføreFlyktninger fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger med mottar = false skal ikke ha en periode`() {
        val periodeMedMottarFalse = """
            "supplerendestønadflyktninger": {
                "mottar": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(supplerendestønadflyktninger = periodeMedMottarFalse))
            .valider() shouldContain "SupplerendeStønadUføreFlyktninger med mottar = false kan ikke ha noen periode"
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "supplerendestønadflyktninger": {
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
            søknad(tiltak = tiltak, supplerendestønadflyktninger = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "supplerendestønadflyktninger": {
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
            søknad(tiltak = tiltak, supplerendestønadflyktninger = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "supplerendestønadflyktninger": {
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
            søknad(tiltak = tiltak, supplerendestønadflyktninger = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "SupplerendeStønadUføreFlyktninger periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "supplerendestønadflyktninger": {
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
            søknad(tiltak = tiltak, supplerendestønadflyktninger = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "SupplerendeStønadUføreFlyktninger periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `SupplerendeStønadUføreFlyktninger med mottar = true må ha en periode`() {
        val mottarTrueUtenPeriode = """
            "supplerendestønadflyktninger": {
                "mottar": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(supplerendestønadflyktninger = mottarTrueUtenPeriode))
            .valider() shouldContain "SupplerendeStønadUføreFlyktninger med mottar = true må ha periode"
    }
}
