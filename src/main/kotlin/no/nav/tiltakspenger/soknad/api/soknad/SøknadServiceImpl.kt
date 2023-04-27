package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadTilJoarkDTO
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class SøknadServiceImpl(
    private val pdfService: PdfService,
    private val joarkService: JoarkService,
) : SøknadService {
    override suspend fun opprettDokumenterOgArkiverIJoark(søknad: SøknadFraGuiDTO, fnr: String, person: PersonDTO, vedlegg: List<Vedlegg>): String {
        val søknadTilJoarkDTO = SøknadTilJoarkDTO.toDTO(søknad, fnr, person)
        val pdf = pdfService.lagPdf(søknadTilJoarkDTO)
        val vedleggSomPdfer = pdfService.konverterVedlegg(vedlegg)
        return joarkService.sendPdfTilJoark(pdf = pdf, søknadTilJoarkDTO = søknadTilJoarkDTO, fnr = fnr, vedlegg = vedleggSomPdfer)
    }
}
