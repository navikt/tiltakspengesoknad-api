package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService

class SøknadServiceImpl(
    private val pdfService: PdfService,
    private val joarkService: JoarkService,
) : SøknadService {
    override suspend fun lagPdfOgSendTilJoark(søknadDTO: SøknadDTO, fnr: String): String {
        val pdf = pdfService.lagPdf(søknadDTO)
        return joarkService.sendPdfTilJoark(pdf, søknadDTO, fnr)
    }
}
