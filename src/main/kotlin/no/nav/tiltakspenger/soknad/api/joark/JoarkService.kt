package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface JoarkService {
    suspend fun sendPdfTilJoark(pdf: ByteArray, søknad: Søknad, fnr: String, vedlegg: List<Vedlegg>): String
}
