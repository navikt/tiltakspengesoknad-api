package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime
import java.util.UUID

fun mapSøknad(
    spm: SpørsmålsbesvarelserDTO,
    fnr: String,
    vedlegg: List<Vedlegg>,
): SøknadDbDTO {
    val nå = LocalDateTime.now()
    return SøknadDbDTO(
        id = UUID.randomUUID(),
        versjon = "1",
        søknadSpm = spm,
        vedlegg = vedlegg,
        fnr = fnr,
        sendtTilVedtak = null,
        journalført = null,
        opprettet = nå,
    )
}
