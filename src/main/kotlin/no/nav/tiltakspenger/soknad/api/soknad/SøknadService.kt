package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

interface SøknadService {
    suspend fun lagPdfOgSendTilJoark(søknadDTO: SøknadDTO, fnr: String): String
}
