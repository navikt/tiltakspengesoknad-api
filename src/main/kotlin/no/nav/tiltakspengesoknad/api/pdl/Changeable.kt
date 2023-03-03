package no.nav.tiltakspengesoknad.api.pdl

import java.time.LocalDateTime

interface Changeable {
    val metadata: EndringsMetadata
    val folkeregistermetadata: FolkeregisterMetadata?
}

const val FREG = "FREG"
fun String.isFreg() = this.equals(FREG, ignoreCase = true)

fun getEndringstidspunktOrNull(data: Changeable): LocalDateTime? =
    when {
        data.metadata.master.isFreg() -> data.folkeregistermetadata?.ajourholdstidspunkt
        else -> data.metadata.endringer.nyeste()?.registrert
    }
