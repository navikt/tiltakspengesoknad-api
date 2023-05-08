package no.nav.tiltakspenger.soknad.api.soknad.validering

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

fun søknad(
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
          $pensjonsordning,
          "harBekreftetAlleOpplysninger": true
        }
""".trimMargin()
