package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class IntroduksjonsprogramTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `introduksjonsprogram periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = fraDatoEtterTil))
            .valider() shouldContain "Introduksjonsprogram fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `introduksjonsprogram med deltar = false skal ikke ha en periode`() {
        val periodeMedDeltarFalse = """
            "introduksjonsprogram": {
                "deltar": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = periodeMedDeltarFalse))
            .valider() shouldContain "Introduksjonsprogram uten deltagelse kan ikke ha noen periode"
    }

    @Test
    fun `introduksjonsprogram periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
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
            søknad(tiltak = tiltak, introduksjonsprogram = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `introduksjonsprogram periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
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
            søknad(tiltak = tiltak, introduksjonsprogram = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `introduksjonsprogram periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
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
            søknad(tiltak = tiltak, introduksjonsprogram = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Introduksjonsprogram periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `introduksjonsprogram periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
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
            søknad(tiltak = tiltak, introduksjonsprogram = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Introduksjonsprogram periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `introduksjonsprogram med deltar = true må ha en periode`() {
        val deltarTrueUtenPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = deltarTrueUtenPeriode))
            .valider() shouldContain "Introduksjonsprogram med deltagelse må ha periode"
    }

    @Test
    fun `introduksjonsprogram OG kvalifiseringsprogrammet med deltar = true må ha en periode`() {
        val deltarIntroTrueUtenPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": null
              }
        """.trimIndent()

        val deltarKvpTrueUtenPeriode = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(kvalifiseringsprogram = deltarKvpTrueUtenPeriode, introduksjonsprogram = deltarIntroTrueUtenPeriode),
        )
            .valider() shouldContainExactlyInAnyOrder listOf(
            "Kvalifisering med deltagelse må ha periode",
            "Introduksjonsprogram med deltagelse må ha periode",
        )
    }
}
