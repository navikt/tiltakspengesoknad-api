package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.serialize
import java.security.InvalidParameterException

internal fun String.toSpørsmålsbesvarelserDbJson(): SpørsmålsbesvarelserDTO {
    try {
        return deserialize(this)
    } catch (exception: Exception) {
        throw InvalidParameterException("Det oppstod en feil ved parsing av json for spm: " + exception.message)
    }
}

internal fun SpørsmålsbesvarelserDTO.toDbJson(): String = serialize(this)
