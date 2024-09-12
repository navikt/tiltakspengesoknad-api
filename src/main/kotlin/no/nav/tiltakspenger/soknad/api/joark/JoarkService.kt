package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class JoarkService(
    applicationConfig: ApplicationConfig,
    private val joarkClient: JoarkClient = JoarkClient(applicationConfig),
) {
    suspend fun sendPdfTilJoark(
        pdf: ByteArray,
        søknadDTO: SøknadDTO,
        fnr: String,
        vedlegg: List<Vedlegg>,
        søknadId: SøknadId,
    ): String {
        val journalpost = Journalpost.Søknadspost.from(
            fnr = fnr,
            søknadDTO = søknadDTO,
            pdf = pdf,
            vedlegg = vedlegg,
        )
        return joarkClient.opprettJournalpost(journalpost, søknadId)
    }
}
