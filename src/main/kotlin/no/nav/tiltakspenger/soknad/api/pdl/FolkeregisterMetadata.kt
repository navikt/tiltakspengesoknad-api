package no.nav.tiltakspenger.soknad.api.pdl

import java.time.LocalDateTime

data class FolkeregisterMetadata(
    val aarsak: String?,
    val ajourholdstidspunkt: LocalDateTime?,
    val gyldighetstidspunkt: LocalDateTime?,
    val kilde: String?,
    val opphoerstidspunkt: LocalDateTime?,
    val sekvens: Int?,
)
