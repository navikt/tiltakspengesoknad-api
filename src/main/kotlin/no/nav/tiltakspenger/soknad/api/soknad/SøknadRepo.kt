package no.nav.tiltakspenger.soknad.api.soknad

interface SøknadRepo {
    fun lagre(dto: Søknad)
    fun oppdater(dto: Søknad)
    fun hentAlleSøknadDbDtoSomIkkeErJournalført(): List<Søknad>
    fun hentAlleSøknadDbDtoSomErJournalførtMenIkkeSendtTilVedtak(): List<Søknad>
}
