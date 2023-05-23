package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class HarBekreftetÅSvareSåGodtManKanTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `vi skal kreve at harBekreftetÅSvareSåGodtManKan har blitt bekreftet`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad(harBekreftetÅSvareSåGodtManKanSvar = false))
            .valider() shouldContain "Bruker må bekrefte å svare så godt man kan"
    }
}
