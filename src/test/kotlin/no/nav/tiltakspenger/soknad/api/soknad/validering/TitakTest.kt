package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import no.nav.tiltakspenger.soknad.api.soknad.valider
import org.junit.jupiter.api.Test

internal class TitakTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `tiltak periode må starte før den slutter`() {
        val tiltak = """
        "tiltak": {
            "aktivitetId": "123",
            "søkerHeleTiltaksperioden": false,
            "periode": {
              "fra": "2025-01-02",
              "til": "2025-01-01"
            },
            "arrangør": "test",
            "type": "test"
          }
        """.trimIndent()

        deserialize<SpørsmålsbesvarelserDTO>(
            søknad(tiltak = tiltak),
        )
            .valider() shouldContain "Tiltak fra dato kan ikke være etter til dato"
    }
}
