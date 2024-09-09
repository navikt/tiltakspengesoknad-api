package no.nav.tiltakspenger.soknad.api.soknad

interface SøknadRepo {
    fun lagre(dto: SøknadDbDTO)
}
