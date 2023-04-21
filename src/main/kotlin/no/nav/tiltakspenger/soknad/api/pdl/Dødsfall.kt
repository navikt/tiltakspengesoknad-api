package no.nav.tiltakspenger.soknad.api.pdl

import java.time.LocalDate

data class Dødsfall(
    val doedsdato: LocalDate?,
    override val folkeregistermetadata: FolkeregisterMetadata,
    override val metadata: EndringsMetadata,
) : Changeable
