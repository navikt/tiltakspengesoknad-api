package no.nav.tiltakspenger.soknad.api.saksbehandlingApi

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.soknad.api.domain.Søknad

// TODO post-mvp jah: Flytt logikken som tilhører denne fra SøknadJobbServiceImpl og inn i denne.
class SendSøknadTilSaksbehandlingApiService(
    applicationConfig: ApplicationConfig,
    private val saksbehandlingApiKlient: SaksbehandlingApiKlient = SaksbehandlingApiKlient(config = applicationConfig),
) {

    suspend fun sendSøknad(søknad: Søknad, journalpostId: String, correlationId: CorrelationId) {
        saksbehandlingApiKlient.sendSøknad(søknadMapper(søknad, journalpostId), correlationId)
    }
}
