package no.nav.tiltakspenger.soknad.api.soknad.validering

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class KvalifiseringprogramTest {

    @Test
    fun `happy case`() {
        shouldNotThrowAny {
            deserialize<SpørsmålsbesvarelserDTO>(søknad())
        }
    }

    @Test
    fun `kvalifiseringsprogram periode fra må være lik eller før fra dato`() {
        val fraDatoEtterTil = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(kvalifiseringsprogram = fraDatoEtterTil))
        }.message shouldContain "Kvalifisering fra dato må være tidligere eller lik til dato"
    }

    @Test
    fun `kvalifiseringsprogram med deltar = false skal ikke ha en periode`() {
        val periodeMedDeltarFalse = """
            "kvalifiseringsprogram": {
                "deltar": false,
                "periode": {
                  "fra": "2025-02-01",
                  "til": "2025-01-01"
                }
              }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(kvalifiseringsprogram = periodeMedDeltarFalse))
        }.message shouldContain "Kvalifisering uten deltagelse kan ikke ha noen periode"
    }

    @Test
    fun `kvalifiseringsprogram periode fra kan ikke være tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2024-01-01",
                  "til": "2025-04-01"
                }
              }
        """.trimIndent()

        val tiltak = """
        "tiltak": {
            "arrangør": "test",
            "type": "test",
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-04-01"
            }
          }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(tiltak = tiltak, kvalifiseringsprogram = fraDatoTidligereEnnTiltakPeriode))
        }.message shouldContain "Kvalifisering fra dato kan ikke være før fra dato på tiltaket"
    }

    @Test
    fun `kvalifiseringsprogram periode til kan ikke være senere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-01-01",
                  "til": "2025-05-01"
                }
              }
        """.trimIndent()

        val tiltak = """
        "tiltak": {
            "arrangør": "test",
            "type": "test",
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-04-01"
            }
          }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(tiltak = tiltak, kvalifiseringsprogram = fraDatoTidligereEnnTiltakPeriode))
        }.message shouldContain "Kvalifisering til dato kan ikke være etter til dato på tiltaket"
    }

    @Test
    fun `kvalifiseringsprogram med deltar = true må ha en periode`() {
        val deltarTrueUtenPeriode = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": null
              }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(kvalifiseringsprogram = deltarTrueUtenPeriode))
        }.message shouldContain "Kvalifisering med deltagelse må ha periode"
    }
}
