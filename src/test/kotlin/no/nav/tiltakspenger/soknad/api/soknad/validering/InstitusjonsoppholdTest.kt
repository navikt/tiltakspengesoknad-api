package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class InstitusjonsoppholdTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `institusjonsopphold periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(institusjonsopphold = fraDatoEtterTil))
            .valider() shouldContain "Institusjonsopphold fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `institusjonsopphold med borPåInstitusjon = false skal ikke ha en periode`() {
        val periodeMedDeltarFalse = """
            "institusjonsopphold": {
                "borPåInstitusjon": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(institusjonsopphold = periodeMedDeltarFalse))
            .valider() shouldContain "Institusjonsopphold uten deltagelse kan ikke ha noen periode"
    }

    @Test
    fun `institusjonsopphold periode kan starte tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
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
            søknad(tiltak = tiltak, institusjonsopphold = fraDatoTidligereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `institusjonsopphold periode kan slutte senere enn tiltakets periode`() {
        val tilDatoSenereEnnTiltakPeriode = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
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
            søknad(tiltak = tiltak, institusjonsopphold = tilDatoSenereEnnTiltakPeriode),
        ).valider() shouldBe emptyList()
    }

    @Test
    fun `institusjonsopphold periode til kan ikke være tidligere enn tiltakets periode`() {
        val tilDatoTidligereEnnTiltakPeriode = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
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
            søknad(tiltak = tiltak, institusjonsopphold = tilDatoTidligereEnnTiltakPeriode),
        ).valider() shouldContain "Institusjonsopphold periode kan ikke være tidligere enn tiltakets periode"
    }

    @Test
    fun `institusjonsopphold periode kan ikke være senere enn tiltakets periode`() {
        val fraDatoSenereEnnTiltakPeriode = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
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
            søknad(tiltak = tiltak, institusjonsopphold = fraDatoSenereEnnTiltakPeriode),
        ).valider() shouldContain "Institusjonsopphold periode kan ikke være senere enn tiltakets periode"
    }

    @Test
    fun `institusjonsopphold med borPåInstitusjon = true må ha en periode`() {
        val deltarTrueUtenPeriode = """
            "institusjonsopphold": {
                "borPåInstitusjon": true,
                "periode": null
              }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(søknad(institusjonsopphold = deltarTrueUtenPeriode))
            .valider() shouldContain "Institusjonsopphold med deltagelse må ha periode"
    }
}
