package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
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
            person: PersonDTO,
            innsendingTidspunkt: LocalDateTime,
        ): SøknadDTO {
            return SøknadDTO(
                acr = acr,
                versjon = "4",
                spørsmålsbesvarelser = spørsmålsbesvarelser,
                vedleggsnavn = vedleggsnavn,
                personopplysninger = Personopplysninger(
                    ident = fnr,
                    fornavn = person.fornavn,
                    etternavn = person.etternavn,
                ),
                innsendingTidspunkt = innsendingTidspunkt,
            )
        }
    }
}
