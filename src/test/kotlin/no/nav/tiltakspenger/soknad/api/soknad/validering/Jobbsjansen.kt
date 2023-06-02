package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class Jobbsjansen {
    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `Jobbsjansen periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "jobbsjansen": {
                "mottar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(jobbsjansen = fraDatoEtterTil))
            .valider() shouldContain "Jobbsjansen fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `Jobbsjansen med mottar = false skal ikke ha en periode`() {
        val periodeMedMottarFalse = """
            "jobbsjansen": {
                "mottar": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(jobbsjansen = periodeMedMottarFalse))
            .valider() shouldContain "Jobbsjansen med mottar = false kan ikke ha noen periode"
    }

    @Test
    fun `Jobbsjansen periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "jobbsjansen": {
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
            søknad(tiltak = tiltak, jobbsjansen = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `Jobbsjansen periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "jobbsjansen": {
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
            søknad(tiltak = tiltak, jobbsjansen = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `Jobbsjansen periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "jobbsjansen": {
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
            søknad(tiltak = tiltak, jobbsjansen = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Jobbsjansen periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `Jobbsjansen periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "jobbsjansen": {
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
            søknad(tiltak = tiltak, jobbsjansen = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Jobbsjansen periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `Jobbsjansen med mottar = true må ha en periode`() {
        val mottarTrueUtenPeriode = """
            "jobbsjansen": {
                "mottar": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(jobbsjansen = mottarTrueUtenPeriode))
            .valider() shouldContain "Jobbsjansen med mottar = true må ha periode"
    }
}

