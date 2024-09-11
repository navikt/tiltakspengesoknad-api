package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.serialize
import java.security.InvalidParameterException

internal fun String.toSøknadDbJson(): SøknadDTO {
    try {
        return deserialize(this)
    } catch (exception: Exception) {
        throw InvalidParameterException("Det oppstod en feil ved parsing av json for søknad: " + exception.message)
    }
}

internal fun SøknadDTO.toDbJson(): String = serialize(this)
