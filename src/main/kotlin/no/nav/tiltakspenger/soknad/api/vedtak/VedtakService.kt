package no.nav.tiltakspenger.soknad.api.vedtak

import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.soknad.api.domain.Søknad

interface VedtakService {
    suspend fun sendSøknad(søknad: Søknad, correlationId: CorrelationId)
}
