package no.nav.tiltakspenger.soknad.api.pdl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test

class SøkerFraPDLTest {

    @Test
    fun mappe() {
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .addModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build()

        val respons: SøkerRespons = mapper.readValue(json)
    }

    private val json = """
        {
          "data": {
            "hentPerson": {
              "adressebeskyttelse": [],
              "forelderBarnRelasjon": [
                {
                  "relatertPersonsIdent": "14820298013",
                  "relatertPersonsRolle": "BARN",
                  "folkeregistermetadata": {
                    "aarsak": null,
                    "ajourholdstidspunkt": "2020-12-23T10:26:08",
                    "gyldighetstidspunkt": "2002-02-14T00:00",
                    "kilde": "KILDE_DSF",
                    "opphoerstidspunkt": null,
                    "sekvens": null
                  },
                  "metadata": {
                    "endringer": [
                      {
                        "kilde": "KILDE_DSF",
                        "registrert": "2021-02-22T22:02:51",
                        "registrertAv": "Folkeregisteret",
                        "systemkilde": "FREG",
                        "type": "OPPRETT"
                      }
                    ],
                    "master": "FREG",
                    "opplysningsId": "94248a32-f06f-4f2e-9ee7-dd6556d87d91",
                    "historisk": false
                  }
                },
                {
                  "relatertPersonsIdent": "19825197225",
                  "relatertPersonsRolle": "MOR",
                  "folkeregistermetadata": {
                    "aarsak": null,
                    "ajourholdstidspunkt": "2020-12-23T12:34:40",
                    "gyldighetstidspunkt": "1977-03-28T00:00",
                    "kilde": "KILDE_DSF",
                    "opphoerstidspunkt": null,
                    "sekvens": null
                  },
                  "metadata": {
                    "endringer": [
                      {
                        "kilde": "KILDE_DSF",
                        "registrert": "2021-02-22T22:02:51",
                        "registrertAv": "Folkeregisteret",
                        "systemkilde": "FREG",
                        "type": "OPPRETT"
                      }
                    ],
                    "master": "FREG",
                    "opplysningsId": "02333258-07f6-49e1-ba50-77cfd676462f",
                    "historisk": false
                  }
                },
                {
                  "relatertPersonsIdent": "24835197391",
                  "relatertPersonsRolle": "FAR",
                  "folkeregistermetadata": {
                    "aarsak": null,
                    "ajourholdstidspunkt": "2020-12-23T12:34:40",
                    "gyldighetstidspunkt": "1977-03-28T00:00",
                    "kilde": "KILDE_DSF",
                    "opphoerstidspunkt": null,
                    "sekvens": null
                  },
                  "metadata": {
                    "endringer": [
                      {
                        "kilde": "KILDE_DSF",
                        "registrert": "2021-02-22T22:02:51",
                        "registrertAv": "Folkeregisteret",
                        "systemkilde": "FREG",
                        "type": "OPPRETT"
                      }
                    ],
                    "master": "FREG",
                    "opplysningsId": "a73c0705-acd9-4819-849b-f168d82c20de",
                    "historisk": false
                  }
                }
              ],
              "navn": [
                {
                  "fornavn": "USJENERT",
                  "mellomnavn": null,
                  "etternavn": "KABIN",
                  "folkeregistermetadata": {
                    "aarsak": "Patch",
                    "ajourholdstidspunkt": "2022-02-14T15:22:53",
                    "gyldighetstidspunkt": "2022-02-14T15:22:53",
                    "kilde": "Synutopia",
                    "opphoerstidspunkt": null,
                    "sekvens": null
                  },
                  "metadata": {
                    "endringer": [
                      {
                        "kilde": "Synutopia",
                        "registrert": "2022-02-14T15:22:58",
                        "registrertAv": "Folkeregisteret",
                        "systemkilde": "FREG",
                        "type": "OPPRETT"
                      }
                    ],
                    "master": "FREG",
                    "opplysningsId": "1bd4aa0c-783f-406c-935b-f35c9dc32926",
                    "historisk": false
                  }
                }
              ],
              "doedsfall": []
            }
          },
          "extensions": {
            "warnings": [
              {
                "query": "N/A",
                "id": "behandlingsnummer_header_missing",
                "message": "header mangler",
                "details": "header for 'behandlingsnummer' mangler"
              }
            ]
          }
        }
    """.trimIndent()
}
