package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class HarBekreftetAlleOpplysningerTest {

    @Test
    fun `happy case`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad()).valider() shouldBe emptyList()
    }

    @Test
    fun `vi skal kreve at harBekreftetAlleOpplysninger har blitt bekreftet`() {
        deserialize<SpørsmålsbesvarelserDTO>(søknad(harBekreftetAlleOpplysningerSvar = false))
            .valider() shouldContain "Bruker må bekrefte å ha oppgitt riktige opplysninger"
    }
}
