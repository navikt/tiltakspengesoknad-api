package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.serialize
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.security.InvalidParameterException

internal fun String.vedleggDbJson(): List<Vedlegg> {
    try {
        return deserialize(this)
    } catch (exception: Exception) {
        throw InvalidParameterException("Det oppstod en feil ved parsing av json for vedlegg: " + exception.message)
    }
}

internal fun List<Vedlegg>.toDbJson(): String = serialize(this)
