package no.nav.tiltakspenger.soknad.api.soknad.validering

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.domain.Personopplysninger
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import java.time.LocalDateTime

fun søknad(
    spørsmålsbesvarelser: SpørsmålsbesvarelserDTO = spørsmålsbesvarelser(),
    personopplysninger: Personopplysninger = personopplysninger(),
) = SøknadDTO(
    id = SøknadId.random().toString(),
    versjon = "4",
    acr = "Level4",
    spørsmålsbesvarelser = spørsmålsbesvarelser,
    vedleggsnavn = listOf("test.pdf"),
    personopplysninger = personopplysninger,
    innsendingTidspunkt = LocalDateTime.now(),
)

private fun personopplysninger() =
    Personopplysninger(
        ident = "12345678910",
        fornavn = "Test",
        etternavn = "Testesen",
    )
