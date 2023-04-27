package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.SøknadTilJoarkDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface PdfService {
    suspend fun lagPdf(søknadTilJoarkDTO: SøknadTilJoarkDTO): ByteArray
    suspend fun konverterVedlegg(vedlegg: List<Vedlegg>): List<Vedlegg>
}
