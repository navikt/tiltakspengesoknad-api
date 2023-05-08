package no.nav.tiltakspenger.soknad.api.soknad.validering

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import org.junit.jupiter.api.Test

internal class HarBekreftetAlleOpplysningerTest {

    @Test
    fun `happy case`() {
        shouldNotThrowAny {
            deserialize<SpørsmålsbesvarelserDTO>(søknad())
        }
    }

    @Test
    fun `vi skal kreve at harBekreftetAlleOpplysninger har blitt bekreftet`() {
        shouldThrow<ValueInstantiationException> {
            deserialize<SpørsmålsbesvarelserDTO>(søknad(harBekreftetAlleOpplysningerSvar = false))
        }.message shouldContain Regex("Bruker må bekrefte å ha oppgitt riktige opplysninger")
    }
}
