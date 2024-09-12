package no.nav.tiltakspenger.soknad.api.vedlegg

import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.serialize
import java.security.InvalidParameterException

data class Vedlegg(
    val filnavn: String,
    val contentType: String,
    val dokument: ByteArray,
    val brevkode: String = "S1",
)

fun String.vedleggDbJson(): List<Vedlegg> {
    try {
        return deserialize(this)
    } catch (exception: Exception) {
        throw InvalidParameterException("Det oppstod en feil ved parsing av json for vedlegg: " + exception.message)
    }
}

fun List<Vedlegg>.toDbJson(): String = serialize(this)
