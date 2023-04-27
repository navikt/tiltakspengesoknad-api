package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadTilJoarkDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkServiceImpl(
    private val joark: Joark,
) : JoarkService {
    override suspend fun sendPdfTilJoark(pdf: ByteArray, søknadTilJoarkDTO: SøknadTilJoarkDTO, fnr: String, vedlegg: List<Vedlegg>): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
//            saksnummer = "",
            søknadTilJoarkDTO = søknadTilJoarkDTO,
            pdf = pdf,
            vedlegg = vedlegg,
        )
        return joark.opprettJournalpost(journalpost)
    }
}
