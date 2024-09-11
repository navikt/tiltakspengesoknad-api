package no.nav.tiltakspenger.soknad.api.soknad

interface SøknadRepo {
    fun lagre(dto: SøknadDbDTO)
    fun oppdater(dto: SøknadDbDTO)
    fun hentAlleSøknadDbDtoSomIkkeErJournalført(): List<SøknadDbDTO>
    fun hentAlleSøknadDbDtoSomErJournalførtMenIkkeSendtTilVedtak(): List<SøknadDbDTO>
}
