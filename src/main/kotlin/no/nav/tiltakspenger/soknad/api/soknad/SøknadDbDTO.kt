package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

data class SøknadDbDTO(
    val id: SøknadId,
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
