package no.nav.tiltakspenger.soknad.api.soknad

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import no.nav.tiltakspenger.soknad.api.deserialize
import org.junit.jupiter.api.Test

internal class SpørsmålsbesvarelserDTOTest {

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
        }.message shouldContain Regex("Kvalifisering fra dato må være tidligere eller lik til dato")
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
        }.message shouldContain Regex("Kvalifisering uten deltagelse kan ikke ha noen periode")
    }

    @Test
    fun `kvalifiseringsprogram periode fra kan ikke være tidligere enn tiltakets periode`() {
        val fraDatoTidligereEnnTiltakPeriode = """
            "kvalifiseringsprogram": {
                "deltar": true,
                "periode": {
                  "fra": "2025-01-01",
                  "til": "2025-04-01"
                }
              }
        """.trimIndent()

        val tiltak = """
        "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-02-01",
              "til": "2025-04-01"
            }
          }
        """.trimIndent()

        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(tiltak = tiltak, kvalifiseringsprogram = fraDatoTidligereEnnTiltakPeriode))
        }.message shouldContain Regex("Kvalifisering fra dato kan ikke være før fra dato på tiltaket")
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
        }.message shouldContain Regex("Kvalifisering til dato kan ikke være etter til dato på tiltaket")
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
        }.message shouldContain Regex("Kvalifisering med deltagelse må ha periode")
    }

    private fun tiltak() = """
        "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun barnetillegg() = """
        "barnetillegg": {
            "manueltRegistrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "etternavn": "Test",
                "fødselsdato": "2025-01-01",
                "bostedsland": "Test"
              }
            ],
            "søkerOmBarnetillegg": true,
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2025-01-01",
                "etternavn": "Testesen"
              }
            ],
            "ønskerÅSøkeBarnetilleggForAndreBarn": true
          }
    """.trimIndent()

    private fun etterlønn() = """
        "etterlønn": {
            "mottarEllerSøktEtterlønn": true,
            "utbetaler": "Test",
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun institusjonsopphold() = """
        "institusjonsopphold": {
            "borPåInstitusjon": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun introduksjonsprogram() = """
        "introduksjonsprogram": {
            "deltar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun kvalifiseringsprogram() = """
        "kvalifiseringsprogram": {
            "deltar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun pensjonsordning() = """
        "pensjonsordning": {
            "utbetaler": "Test",
            "mottarEllerSøktPensjonsordning": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
    """.trimIndent()

    private fun søknad(
        tiltak: String = tiltak(),
        barneTillegg: String = barnetillegg(),
        etterlønn: String = etterlønn(),
        institusjonsopphold: String = institusjonsopphold(),
        introduksjonsprogram: String = introduksjonsprogram(),
        kvalifiseringsprogram: String = kvalifiseringsprogram(),
        pensjonsordning: String = pensjonsordning(),
    ) = """
        {
          $tiltak,
          $barneTillegg,
          $etterlønn,
          $institusjonsopphold,
          $introduksjonsprogram,
          $kvalifiseringsprogram,
          $pensjonsordning
        }
    """.trimMargin()
}
