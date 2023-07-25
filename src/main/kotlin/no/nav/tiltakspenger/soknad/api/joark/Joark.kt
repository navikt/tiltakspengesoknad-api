package no.nav.tiltakspenger.soknad.api.joark

interface Joark {
    suspend fun opprettJournalpost(dokumentInnhold: Journalpost, callId: String): String // JournalpostId
}
