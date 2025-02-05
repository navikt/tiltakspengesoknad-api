package no.nav.tiltakspenger.soknad.api.soknad.routes

import java.time.LocalDateTime

data class SøknadResponse(
    val journalpostId: String,
    val innsendingTidspunkt: LocalDateTime,
)
