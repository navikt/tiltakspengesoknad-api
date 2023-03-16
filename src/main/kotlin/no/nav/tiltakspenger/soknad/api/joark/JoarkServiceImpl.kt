package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.Søknad

class JoarkServiceImpl(
    private val joark: Joark,
) : JoarkService {
    override suspend fun sendPdfTilJoark(pdf: ByteArray, søknad: Søknad, fnr: String): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
//            saksnummer = "",
            søknad = søknad,
            pdf = pdf,
        )
        return joark.opprettJournalpost(journalpost)
    }
}
