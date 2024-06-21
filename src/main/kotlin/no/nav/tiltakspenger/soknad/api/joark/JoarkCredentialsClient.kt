package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.client.HttpClient
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.extensions.getAccessTokenOrThrow
import no.nav.tiltakspenger.soknad.api.httpClientWithRetry

class JoarkCredentialsClient(
    config: ApplicationConfig,
    httpClient: HttpClient = httpClientWithRetry(timeout = 10L),
) {
    val joarkScope = config.property("scope.joark").getString()
    private val oauth2CredentialsClient = checkNotNull(ClientConfig(config, httpClient).clients["azure"])

    suspend fun getToken(): String {
        val clientCredentialsGrant = oauth2CredentialsClient.clientCredentials(joarkScope)
        val token = clientCredentialsGrant.getAccessTokenOrThrow()
        return token
    }
}
