package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import no.nav.tiltakspenger.soknad.api.soknad.valider
import org.junit.jupiter.api.Test

internal class BarnetilleggTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `fødselsdato manuelle barn kan ikke være mer enn 16 år tidligere enn tiltaket`() {
        val fraDatoEldreEnn16år = """
        "barnetillegg": {
            "manueltRegistrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "etternavn": "Test",
                "fødselsdato": "2008-12-31",
                "bostedsland": "Test"
              }
            ],
            "søkerOmBarnetillegg": true,
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2009-01-01",
                "etternavn": "Testesen"
              }
            ],
            "ønskerÅSøkeBarnetilleggForAndreBarn": true
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
            søknad(
                tiltak = tiltak,
                barneTillegg = fraDatoEldreEnn16år,
            ),
        )
            .valider() shouldContain "Kan ikke søke for manuelle barn som er mer enn 16 år når tiltaket starter"
    }

    @Test
    fun `fødselsdato registrerte barn kan ikke være mer enn 16 år tidligere enn tiltaket`() {
        val fraDatoEldreEnn16år = """
        "barnetillegg": {
            "manueltRegistrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "etternavn": "Test",
                "fødselsdato": "2009-01-01",
                "bostedsland": "Test"
              }
            ],
            "søkerOmBarnetillegg": true,
            "registrerteBarnSøktBarnetilleggFor": [
              {
                "fornavn": "Test",
                "fødselsdato": "2008-12-31",
                "etternavn": "Testesen"
              }
            ],
            "ønskerÅSøkeBarnetilleggForAndreBarn": true
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
            søknad(
                tiltak = tiltak,
                barneTillegg = fraDatoEldreEnn16år,
            ),
        )
            .valider() shouldContain "Kan ikke søke for registrerte barn som er mer enn 16 år når tiltaket starter"
    }
}
