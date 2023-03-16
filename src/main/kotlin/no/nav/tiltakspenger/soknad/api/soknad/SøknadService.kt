package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.Søknad

interface SøknadService {
    suspend fun lagPdfOgSendTilJoark(søknad: Søknad, fnr: String)
}
