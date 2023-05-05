package no.nav.tiltakspenger.soknad.api.soknad.validering

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class IntroduksjonsprogramTest {

    @Test
    fun `happy case`() {
        shouldNotThrowAny {
            deserialize<SpørsmålsbesvarelserDTO>(søknad())
        }
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

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = fraDatoEtterTil))
        }.message shouldContain Regex("Introduksjonsprogram fra dato må være tidligere eller lik til dato")
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

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = periodeMedDeltarFalse))
        }.message shouldContain Regex("Introduksjonsprogram uten deltagelse kan ikke ha noen periode")
    }

    @Test
    fun `introduksjonsprogram periode fra kan ikke være tidligere enn tiltakets periode`() {
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
            }
          }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(tiltak = tiltak, introduksjonsprogram = fraDatoTidligereEnnTiltakPeriode))
        }.message shouldContain Regex("Introduksjonsprogram fra dato kan ikke være før fra dato på tiltaket")
    }

    @Test
    fun `introduksjonsprogram periode til kan ikke være senere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-01-01",
                  "til": "2025-05-01"
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
            }
          }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(tiltak = tiltak, introduksjonsprogram = fraDatoTidligereEnnTiltakPeriode))
        }.message shouldContain Regex("Introduksjonsprogram til dato kan ikke være etter til dato på tiltaket")
    }

    @Test
    fun `introduksjonsprogram med deltar = true må ha en periode`() {
        val deltarTrueUtenPeriode = """
            "introduksjonsprogram": {
                "deltar": true,
                "periode": null
              }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(introduksjonsprogram = deltarTrueUtenPeriode))
        }.message shouldContain Regex("Introduksjonsprogram med deltagelse må ha periode")
    }
}
