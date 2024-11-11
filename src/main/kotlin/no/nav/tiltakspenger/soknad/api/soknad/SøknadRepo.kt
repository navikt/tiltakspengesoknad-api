package no.nav.tiltakspenger.soknad.api.soknad

interface SøknadRepo {
    fun lagre(dto: MottattSøknad)
    fun oppdater(dto: MottattSøknad)
    fun hentAlleSøknadDbDtoSomIkkeErJournalført(): List<MottattSøknad>
    fun hentSøknaderSomSkalSendesTilSaksbehandlingApi(): List<MottattSøknad>
}
