package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.serialize
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import java.security.InvalidParameterException
import java.time.LocalDateTime

data class Personopplysninger(
    val ident: String,
    val fornavn: String,
    val etternavn: String,
)

data class SøknadDTO(
    val id: String = SøknadId.random().toString(),
    val acr: String,
    val versjon: String,
    val spørsmålsbesvarelser: SpørsmålsbesvarelserDTO,
    val vedleggsnavn: List<String>,
    val personopplysninger: Personopplysninger,
    val innsendingTidspunkt: LocalDateTime,
) {
    companion object {
        fun toDTO(
            acr: String,
            spørsmålsbesvarelser: SpørsmålsbesvarelserDTO,
            vedleggsnavn: List<String>,
            fnr: String,
            fornavn: String,
            etternavn: String,
            innsendingTidspunkt: LocalDateTime,
        ): SøknadDTO {
            return SøknadDTO(
                acr = acr,
                versjon = "4",
                spørsmålsbesvarelser = spørsmålsbesvarelser,
                vedleggsnavn = vedleggsnavn,
                personopplysninger = Personopplysninger(
                    ident = fnr,
                    fornavn = fornavn,
                    etternavn = etternavn,
                ),
                innsendingTidspunkt = innsendingTidspunkt,
            )
        }
    }
}

fun String.toSøknadDbJson(): SøknadDTO {
    try {
        return deserialize(this)
    } catch (exception: Exception) {
        throw InvalidParameterException("Det oppstod en feil ved parsing av json for søknad: " + exception.message)
    }
}

fun SøknadDTO.toDbJson(): String = serialize(this)
