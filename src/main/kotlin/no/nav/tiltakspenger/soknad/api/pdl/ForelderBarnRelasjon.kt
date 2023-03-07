package no.nav.tiltakspenger.soknad.api.pdl

enum class ForelderBarnRelasjonRolle {
    BARN,
}

data class ForelderBarnRelasjon(
    val relatertPersonsIdent: String?,
    val relatertPersonsRolle: ForelderBarnRelasjonRolle,
    override val folkeregistermetadata: FolkeregisterMetadata?,
    override val metadata: EndringsMetadata,
) : Changeable
