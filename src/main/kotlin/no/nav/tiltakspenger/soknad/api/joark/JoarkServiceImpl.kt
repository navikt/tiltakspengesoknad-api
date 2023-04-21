package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

class JoarkServiceImpl(
    private val joark: Joark,
) : JoarkService {
    override suspend fun sendPdfTilJoark(pdf: ByteArray, søknadDTO: SøknadDTO, fnr: String): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
//            saksnummer = "",
            søknadDTO = søknadDTO,
            pdf = pdf,
        )
        return joark.opprettJournalpost(journalpost)
    }
}
