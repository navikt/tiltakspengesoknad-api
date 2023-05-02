package no.nav.tiltakspenger.soknad.api.vedlegg

data class Vedlegg(
    val filnavn: String,
    val contentType: String,
    val dokument: ByteArray,
    val brevkode: String = "S1",
)
