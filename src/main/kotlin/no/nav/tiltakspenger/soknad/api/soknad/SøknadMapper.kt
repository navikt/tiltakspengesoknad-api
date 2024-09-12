package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

fun mapSøknad(
    spm: SpørsmålsbesvarelserDTO,
    acr: String,
    fnr: String,
    vedlegg: List<Vedlegg>,
): SøknadDbDTO {
    val nå = LocalDateTime.now()
    return SøknadDbDTO(
        id = SøknadId.random(),
        versjon = "1",
        søknad = null,
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
