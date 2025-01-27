package no.nav.tiltakspenger.soknad.api.joark

import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkService(
    private val joarkClient: JoarkClient,
) {
    suspend fun sendPdfTilJoark(
        pdf: ByteArray,
        søknad: Søknad,
        fnr: String,
        vedlegg: List<Vedlegg>,
        søknadId: SøknadId,
        callId: String,
        journalforendeEnhet: String?,
        saksnummer: String?,
    ): String {
        val journalpost = JournalpostRequest.from(
            fnr = fnr,
            søknad = søknad,
            pdf = pdf,
            vedlegg = vedlegg,
            journalforendeEnhet = journalforendeEnhet,
            saksnummer = saksnummer,
        )
        return joarkClient.opprettJournalpost(journalpost, søknadId, callId)
    }
}
