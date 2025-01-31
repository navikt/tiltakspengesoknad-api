package no.nav.tiltakspenger.soknad.api.saksbehandlingApi

import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.soknad.api.domain.Søknad

// TODO post-mvp jah: Flytt logikken som tilhører denne fra SøknadJobbServiceImpl og inn i denne.
class SendSøknadTilSaksbehandlingApiService(
    private val saksbehandlingApiKlient: SaksbehandlingApiKlient,
) {
    suspend fun sendSøknad(søknad: Søknad, journalpostId: String, saksnummer: String, correlationId: CorrelationId) {
        saksbehandlingApiKlient.sendSøknad(
            søknadMapper(
                søknad = søknad,
                jounalpostId = journalpostId,
                saksnummer = saksnummer,
            ),
            correlationId,
        )
    }

    suspend fun hentEllerOpprettSaksnummer(fnr: Fnr, correlationId: CorrelationId): String {
        return saksbehandlingApiKlient.hentEllerOpprettSaksnummer(fnr, correlationId)
    }
}
