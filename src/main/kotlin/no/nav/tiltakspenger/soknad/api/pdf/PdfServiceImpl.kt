package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

class PdfServiceImpl(
    private val pdfGenerator: PdfGenerator,
) : PdfService {
    override suspend fun lagPdf(søknadDTO: SøknadDTO) =
        pdfGenerator.genererPdf(søknadDTO)
}
