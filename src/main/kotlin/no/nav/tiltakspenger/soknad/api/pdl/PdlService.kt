package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.server.config.ApplicationConfig

class PdlService(
    applicationConfig: ApplicationConfig,
    pdlClientTokenX: PdlClientTokenX = PdlClientTokenX(config = applicationConfig),
    pdlClientCredentials: PdlCredentialsClient = PdlCredentialsClient(config = applicationConfig),
) {
    private var pdlClientTokenX: PdlClientTokenX
    private var pdlClientCredentials: PdlCredentialsClient

    init {
        this.pdlClientTokenX = pdlClientTokenX
        this.pdlClientCredentials = pdlClientCredentials
    }

    suspend fun hentPersonaliaMedBarn(fødselsnummer: String, subjectToken: String): PersonDTO {
        val result = pdlClientTokenX.fetchSøker(fødselsnummer = fødselsnummer, subjectToken = subjectToken)
        if (result.isSuccess) {
            val person = result.getOrNull()!!.toPerson()
            val barnsIdenter = person.barnsIdenter()
            val barn = barnsIdenter.map { barnsIdent ->
                pdlClientCredentials.fetchBarn(barnsIdent).getOrNull()?.toPerson()
            }.mapNotNull { it }
            return person.toPersonDTO(barn)
        }

        throw IllegalStateException("Noe gikk galt under kall til PDL")
    }
}
