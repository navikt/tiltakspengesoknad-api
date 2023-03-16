package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.Søknad

class PdfServiceImpl(
    private val pdfGenerator: PdfGenerator,
) : PdfService {
    override suspend fun lagPdf(søknad: Søknad) =
        pdfGenerator.genererPdf(søknad)
}
