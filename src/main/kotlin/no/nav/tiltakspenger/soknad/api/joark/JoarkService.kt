package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadTilJoarkDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface JoarkService {
    suspend fun sendPdfTilJoark(pdf: ByteArray, søknadTilJoarkDTO: SøknadTilJoarkDTO, fnr: String, vedlegg: List<Vedlegg>): String
}
