package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime
import java.util.UUID

data class SøknadDbDTO(
    val id: UUID,
    val versjon: String,
    val søknad: SøknadDTO?,
    val søknadSpm: SpørsmålsbesvarelserDTO,
    val vedlegg: List<Vedlegg>,
    val acr: String,
    val fnr: String,
    val fornavn: String?,
    val etternavn: String?,
    val sendtTilVedtak: LocalDateTime?,
    val journalført: LocalDateTime?,
    val journalpostId: String?,
    val opprettet: LocalDateTime,
)
