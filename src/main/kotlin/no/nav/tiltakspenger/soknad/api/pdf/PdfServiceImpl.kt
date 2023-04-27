package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.SøknadTilJoarkDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class PdfServiceImpl(
    private val pdfGenerator: PdfGenerator,
) : PdfService {
    override suspend fun lagPdf(søknadTilJoarkDTO: SøknadTilJoarkDTO) =
        pdfGenerator.genererPdf(søknadTilJoarkDTO)
    override suspend fun konverterVedlegg(vedlegg: List<Vedlegg>) =
        pdfGenerator.konverterVedlegg(vedlegg)
}
