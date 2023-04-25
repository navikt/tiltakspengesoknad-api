package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class SøknadServiceImpl(
    private val pdfService: PdfService,
    private val joarkService: JoarkService,
) : SøknadService {
    override suspend fun opprettDokumenterOgArkiverIJoark(søknad: SøknadRequest, fnr: String, person: PersonDTO, vedlegg: List<Vedlegg>): String {
        val søknadDTO = SøknadDTO.toDTO(søknad, fnr, person)
        val pdf = pdfService.lagPdf(søknadDTO)
        val vedleggSomPdfer = pdfService.konverterVedlegg(vedlegg)
        return joarkService.sendPdfTilJoark(pdf = pdf, søknadDTO = søknadDTO, fnr = fnr, vedlegg = vedleggSomPdfer)
    }
}
