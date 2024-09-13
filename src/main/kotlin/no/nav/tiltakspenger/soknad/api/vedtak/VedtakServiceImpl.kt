package no.nav.tiltakspenger.soknad.api.vedtak

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.soknad.api.domain.Søknad

class VedtakServiceImpl(
    applicationConfig: ApplicationConfig,
    private val vedtakClientImpl: VedtakClient = VedtakClient(config = applicationConfig),
) : VedtakService {

    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("tjenestekall")

    override suspend fun sendSøknad(søknad: Søknad, journalpostId: String, correlationId: CorrelationId) {
        vedtakClientImpl.sendSøknad(søknadMapper(søknad, journalpostId), correlationId)
    }
}
