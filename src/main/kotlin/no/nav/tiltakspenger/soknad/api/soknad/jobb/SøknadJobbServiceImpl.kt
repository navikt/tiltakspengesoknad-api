package no.nav.tiltakspenger.soknad.api.soknad.jobb

import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.soknad.api.Configuration.applicationProfile
import no.nav.tiltakspenger.soknad.api.Profile
import no.nav.tiltakspenger.soknad.api.soknad.jobb.person.PersonGateway
import no.nav.tiltakspenger.soknad.api.soknad.log

class SøknadJobbServiceImpl(
    private val personGateway: PersonGateway,
) : SøknadJobbService {
    override suspend fun journalførLagredeSøknader(correlationId: CorrelationId) {
        if (applicationProfile() == Profile.DEV) {
            val person = personGateway.hentNavnForFnr(Fnr.fromString("12345678910"))
            log.info { "Vi skal hente søknader og journalføre de" }
        }

    }
}
