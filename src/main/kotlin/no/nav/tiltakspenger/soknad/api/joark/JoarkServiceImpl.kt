package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkServiceImpl(
    private val joark: Joark,
) : JoarkService {
    override suspend fun sendPdfTilJoark(
        pdf: ByteArray,
        søknadDTO: SøknadDTO,
        fnr: String,
        vedlegg: List<Vedlegg>,
        callId: String,
    ): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
//            saksnummer = "",
            søknadDTO = søknadDTO,
            pdf = pdf,
            vedlegg = vedlegg,
        )
        return joark.opprettJournalpost(journalpost, callId)
    }
}
