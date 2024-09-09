package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime
import java.util.UUID

data class SøknadDbDTO(
    val id: UUID,
    val versjon: String,
    val søknadSpm: SpørsmålsbesvarelserDTO,
    val vedlegg: List<Vedlegg>,
    val fnr: String,
    val sendtTilVedtak: LocalDateTime?,
    val journalført: LocalDateTime?,
    val opprettet: LocalDateTime,
)
