package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.Søknad

interface JoarkService {
    suspend fun sendPdfTilJoark(pdf: ByteArray, søknad: Søknad, fnr: String): String
}
