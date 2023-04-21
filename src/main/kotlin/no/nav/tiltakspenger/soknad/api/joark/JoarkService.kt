package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface JoarkService {
    suspend fun sendPdfTilJoark(pdf: ByteArray, søknadDTO: SøknadDTO, fnr: String, vedlegg: List<Vedlegg>): String
}
