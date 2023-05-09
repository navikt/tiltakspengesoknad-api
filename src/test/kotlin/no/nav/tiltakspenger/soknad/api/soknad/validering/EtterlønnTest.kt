package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class EtterlønnTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `etterlønn periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": "En som betaler etterlønn",
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(etterlønn = fraDatoEtterTil))
            .valider() shouldContain "Etterlønn fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `etterlønn med mottarEllerSøktEtterlønn = false skal ikke ha en periode eller utbetaler`() {
        val periodeMedDeltarFalse = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": false,
                "utbetaler": "En som betaler etterlønn",
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(etterlønn = periodeMedDeltarFalse))
            .valider() shouldContainExactlyInAnyOrder listOf(
            "En som ikke mottar etterlønn kan ikke ha periode",
            "En som ikke mottar etterlønn kan ikke ha en utbetaler",
        )
    }

    @Test
    fun `etterlønn periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": "En som betaler etterlønn",
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
            søknad(tiltak = tiltak, etterlønn = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `etterlønn periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": "En som betaler etterlønn",
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
            søknad(tiltak = tiltak, etterlønn = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `etterlønn periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": "En som betaler etterlønn",
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
            søknad(tiltak = tiltak, etterlønn = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Etterlønn periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `etterlønn periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": "En som betaler etterlønn",
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
            søknad(tiltak = tiltak, etterlønn = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Etterlønn periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `etterlønn med mottarEllerSøktetterlønn = true må ha en periode og en utbetaler`() {
        val deltarTrueUtenPeriode = """
            "etterlønn": {
                "mottarEllerSøktEtterlønn": true,
                "utbetaler": null,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(etterlønn = deltarTrueUtenPeriode))
            .valider() shouldContainExactlyInAnyOrder listOf(
            "En som mottar etterlønn må ha periode",
            "En som mottar etterlønn må ha en utbetaler",
        )
    }
}
