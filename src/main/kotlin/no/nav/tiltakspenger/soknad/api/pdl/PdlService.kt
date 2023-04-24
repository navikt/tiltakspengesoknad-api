package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.soknad.LOG

class PdlService(
    applicationConfig: ApplicationConfig,
    private val pdlClientTokenX: PdlClientTokenX = PdlClientTokenX(config = applicationConfig),
    private val pdlClientCredentials: PdlCredentialsClient = PdlCredentialsClient(config = applicationConfig),
) {
    suspend fun hentPersonaliaMedBarn(fødselsnummer: String, subjectToken: String): PersonDTO {
        LOG.info { "Nå skal vi hente data fra pdl." }
        val result = pdlClientTokenX.fetchSøker(fødselsnummer = fødselsnummer, subjectToken = subjectToken)
        if (result.isSuccess) {
            LOG.info { "Fjern denne loggingen $result" }
            LOG.info { "Gjør om til person.." }
            val person = result.getOrNull()!!.toPerson()
            LOG.info { "Ferdig!" }
            LOG.info { "henter ut barnas identer.." }
            val barnsIdenter = person.barnsIdenter()
            LOG.info { "Ferdig!" }
            LOG.info { "mapper ut barna.." }
            val barn = barnsIdenter.map { barnsIdent ->
                pdlClientCredentials.fetchBarn(barnsIdent).getOrNull()?.toPerson()
            }.mapNotNull { it }
            LOG.info { "Sånn, ferdig! Det gikk jo aldeles strålende :)" }
            return person.toPersonDTO(barn)
        }

        throw IllegalStateException("Noe gikk galt under kall til PDL $result")
    }
}
