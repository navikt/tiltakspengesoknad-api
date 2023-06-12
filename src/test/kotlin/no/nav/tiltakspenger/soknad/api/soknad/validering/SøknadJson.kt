package no.nav.tiltakspenger.soknad.api.soknad.validering

private fun tiltak() = """
        "tiltak": {
            "aktivitetId": "123",
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            },
            "arrangør": "test",
            "type": "test",
            "typeNavn": "test"
        }
""".trimIndent()

private fun barnetillegg() = """
        "barnetillegg": {
            "manueltRegistrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "etternavn": "Test",
                "fødselsdato": "2025-01-01",
                "oppholdInnenforEøs": true
              }
            ],
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2025-01-01",
                "etternavn": "Testesen",
                "oppholdInnenforEøs": true
              }
            ]
          }
""".trimIndent()

private fun etterlønn() = """
        "etterlønn": {
            "mottar": true
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

private fun sykepenger() = """
        "sykepenger": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
""".trimIndent()

private fun gjenlevendepensjon() = """
        "gjenlevendepensjon": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
""".trimIndent()

private fun supplerendestønadover67år() = """
        "supplerendestønadover67": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
""".trimIndent()

private fun supplerendestønadflyktninger() = """
        "supplerendestønadflyktninger": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
""".trimIndent()

private fun jobbsjansen() = """
        "jobbsjansen": {
            "mottar": true,
            "periode": {
              "fra": "2025-01-01",
              "til": "2025-01-01"
            }
          }
""".trimIndent()

private fun alderspensjon() = """
        "alderspensjon": {
            "mottar": true,
            "fraDato": "2025-01-01"
        }
""".trimIndent()

private fun pensjonsordning() = """
        "pensjonsordning": {
            "mottar": true
        }
""".trimIndent()

private fun mottarAndreUtbetalinger(svar: Boolean) = """
    "mottarAndreUtbetalinger": $svar
""".trimIndent()

private fun harBekreftetAlleOpplysninger(svar: Boolean) = """
    "harBekreftetAlleOpplysninger": $svar
""".trimIndent()

private fun harBekreftetÅSvareSåGodtManKan(svar: Boolean) = """
    "harBekreftetÅSvareSåGodtManKan": $svar
""".trimIndent()

fun søknad(
    tiltak: String = tiltak(),
    barneTillegg: String = barnetillegg(),
    institusjonsopphold: String = institusjonsopphold(),
    introduksjonsprogram: String = introduksjonsprogram(),
    kvalifiseringsprogram: String = kvalifiseringsprogram(),
    pensjonsordning: String = pensjonsordning(),
    mottarAndreUtbetalinger: Boolean = true,
    sykepenger: String = sykepenger(),
    gjenlevendepensjon: String = gjenlevendepensjon(),
    alderspensjon: String = alderspensjon(),
    supplerendestønadover67: String = supplerendestønadover67år(),
    supplerendestønadflyktninger: String = supplerendestønadflyktninger(),
    etterlønn: String = etterlønn(),
    jobbsjansen: String = jobbsjansen(),
    harBekreftetAlleOpplysningerSvar: Boolean = true,
    harBekreftetÅSvareSåGodtManKanSvar: Boolean = true,
) = """
        {
          $tiltak,
          $barneTillegg,
          $institusjonsopphold,
          $introduksjonsprogram,
          $kvalifiseringsprogram,
          ${mottarAndreUtbetalinger(mottarAndreUtbetalinger)},
          $sykepenger,
          $gjenlevendepensjon,
          $alderspensjon,
          $supplerendestønadover67,
          $supplerendestønadflyktninger,
          $pensjonsordning,
          $etterlønn,
          $jobbsjansen,
          ${harBekreftetAlleOpplysninger(harBekreftetAlleOpplysningerSvar)},
          ${harBekreftetÅSvareSåGodtManKan(harBekreftetÅSvareSåGodtManKanSvar)}
        }
""".trimMargin()
