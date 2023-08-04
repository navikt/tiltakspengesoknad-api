package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import java.time.LocalDateTime
import java.util.UUID

data class Personopplysninger(
    val ident: String,
    val fornavn: String,
    val etternavn: String,
)

data class SøknadDTO(
    val id: UUID = UUID.randomUUID(),
    val acr: String,
    val spørsmålsbesvarelser: SpørsmålsbesvarelserDTO,
    val vedleggsnavn: List<String>,
    val personopplysninger: Personopplysninger,
    val innsendingTidspunkt: LocalDateTime,
) {
    companion object {
        fun toDTO(
            spørsmålsbesvarelser: SpørsmålsbesvarelserDTO,
            fnr: String,
            person: PersonDTO,
            acr: String,
            innsendingTidspunkt: LocalDateTime,
            vedleggsnavn: List<String>,
        ): SøknadDTO {
            return SøknadDTO(
                vedleggsnavn = vedleggsnavn,
                spørsmålsbesvarelser = spørsmålsbesvarelser,
                personopplysninger = Personopplysninger(
                    ident = fnr,
                    fornavn = person.fornavn,
                    etternavn = person.etternavn,
                ),
                acr = acr,
                innsendingTidspunkt = innsendingTidspunkt,
            )
        }
    }
}
