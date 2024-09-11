package no.nav.tiltakspenger.soknad.api.soknad.jobb

import no.nav.tiltakspenger.libs.common.CorrelationId

interface SøknadJobbService {
    suspend fun journalførLagredeSøknader(correlationId: CorrelationId)
}
