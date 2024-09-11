package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime
import java.util.UUID

fun mapSøknad(
    spm: SpørsmålsbesvarelserDTO,
    acr: String,
    fnr: String,
    vedlegg: List<Vedlegg>,
): SøknadDbDTO {
    val nå = LocalDateTime.now()
    return SøknadDbDTO(
        id = UUID.randomUUID(),
        versjon = "1",
        søknadSpm = spm,
        vedlegg = vedlegg,
        acr = acr,
        fnr = fnr,
        fornavn = null,
        etternavn = null,
        sendtTilVedtak = null,
        journalført = null,
        journalpostId = null,
        opprettet = nå,
    )
}
