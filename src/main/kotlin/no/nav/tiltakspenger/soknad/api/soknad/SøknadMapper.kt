package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime
import java.util.*

fun mapSøknad(
    spm: SpørsmålsbesvarelserDTO,
    fnr: String,
    vedlegg: List<Vedlegg>,
): Søknad {
    val nå = LocalDateTime.now()
    return Søknad(
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
