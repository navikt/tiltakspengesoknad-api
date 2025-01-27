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
    ): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
            søknad = søknad,
            pdf = pdf,
            vedlegg = vedlegg,
        )
        return joarkClient.opprettJournalpost(journalpost, søknadId, callId)
    }
}
