package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkService(
    applicationConfig: ApplicationConfig,
    private val joarkClient: JoarkClient = JoarkClient(applicationConfig),
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
