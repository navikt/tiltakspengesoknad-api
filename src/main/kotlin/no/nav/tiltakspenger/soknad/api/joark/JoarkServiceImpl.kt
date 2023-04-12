package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkServiceImpl(
    private val joark: Joark,
) : JoarkService {
    override suspend fun sendPdfTilJoark(pdf: ByteArray, søknad: Søknad, fnr: String, vedlegg: List<Vedlegg>): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
//            saksnummer = "",
            søknad = søknad,
            pdf = pdf,
            vedlegg = vedlegg,
        )
        return joark.opprettJournalpost(journalpost)
    }
}
