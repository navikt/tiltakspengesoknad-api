package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

interface JoarkService {
    suspend fun sendPdfTilJoark(pdf: ByteArray, søknadDTO: SøknadDTO, fnr: String): String
}
