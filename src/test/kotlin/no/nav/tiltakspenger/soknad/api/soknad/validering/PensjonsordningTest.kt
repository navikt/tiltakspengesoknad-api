package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class PensjonsordningTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `pensjonsordning periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": "En som betaler pensjon",
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(pensjonsordning = fraDatoEtterTil))
            .valider() shouldContain "Pensjonsordning fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `pensjonsordning med mottarEllerSøktPensjonsordning = false skal ikke ha en periode eller utbetaler`() {
        val periodeMedDeltarFalse = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": false,
                "utbetaler": "En som betaler pensjon",
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(pensjonsordning = periodeMedDeltarFalse))
            .valider() shouldContainExactlyInAnyOrder listOf(
            "En som ikke mottar pensjon kan ikke ha periode",
            "En som ikke mottar pensjon kan ikke ha en utbetaler",
        )
    }

    @Test
    fun `pensjonsordning periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": "En som betaler pensjon",
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
            "type": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, pensjonsordning = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `pensjonsordning periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": "En som betaler pensjon",
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
            "type": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, pensjonsordning = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `pensjonsordning periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": "En som betaler pensjon",
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
            "type": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, pensjonsordning = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Pensjonsordning periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `pensjonsordning periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": "En som betaler pensjon",
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
            "type": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak, pensjonsordning = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Pensjonsordning periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `pensjonsordning med mottarEllerSøktPensjonsordning = true må ha en periode og en utbetaler`() {
        val deltarTrueUtenPeriode = """
            "pensjonsordning": {
                "mottarEllerSøktPensjonsordning": true,
                "utbetaler": null,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(pensjonsordning = deltarTrueUtenPeriode))
            .valider() shouldContainExactlyInAnyOrder listOf(
            "En som mottar pensjon må ha periode",
            "En som mottar pensjon må ha en utbetaler",
        )
    }
}
