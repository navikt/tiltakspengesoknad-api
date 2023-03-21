package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService

class SøknadServiceImpl(
    private val pdfService: PdfService,
    private val joarkService: JoarkService,
) : SøknadService {
    override suspend fun lagPdfOgSendTilJoark(søknad: Søknad, fnr: String): String {
        val pdf = pdfService.lagPdf(søknad)
        return joarkService.sendPdfTilJoark(pdf, søknad, fnr)
    }
}
