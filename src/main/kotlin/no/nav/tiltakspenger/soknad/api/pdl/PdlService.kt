package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

val LOG = KotlinLogging.logger {}

class PdlService(
    applicationConfig: ApplicationConfig,
    private val pdlClientTokenX: PdlClientTokenX = PdlClientTokenX(config = applicationConfig),
    private val pdlClientCredentials: PdlCredentialsClient = PdlCredentialsClient(config = applicationConfig),
) {
    suspend fun hentPersonaliaMedBarn(fødselsnummer: String, subjectToken: String): PersonDTO {
        LOG.error { "Nå skal vi hente data fra pdl." }
        val result = pdlClientTokenX.fetchSøker(fødselsnummer = fødselsnummer, subjectToken = subjectToken)
        if (result.isSuccess) {
            LOG.error { "Fjern denne loggingen $result" }
            LOG.error { "Gjør om til person.." }
            val person = result.getOrNull()!!.toPerson()
            LOG.error { "Ferdig!" }
            LOG.error { "henter ut barnas identer.." }
            val barnsIdenter = person.barnsIdenter()
            LOG.error { "Ferdig!" }
            LOG.error { "mapper ut barna.." }
            val barn = barnsIdenter.map { barnsIdent ->
                pdlClientCredentials.fetchBarn(barnsIdent).getOrNull()?.toPerson()
            }.mapNotNull { it }
            LOG.error { "Sånn, ferdig! Det gikk jo aldeles strålende :)" }
            return person.toPersonDTO(barn)
        }

        throw IllegalStateException("Noe gikk galt under kall til PDL")
    }
}
