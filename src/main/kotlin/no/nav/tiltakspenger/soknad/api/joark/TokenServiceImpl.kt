package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.httpClientCIO

class TokenServiceImpl : TokenService {
    override suspend fun getToken(config: ApplicationConfig): String {
        val joarkScope = config.property("scope.joark").getString()
        val oauth2CredentialsClient = checkNotNull(ClientConfig(config, httpClientCIO()).clients["azure"])
        val clientCredentialsGrant = oauth2CredentialsClient.clientCredentials(joarkScope)
        return clientCredentialsGrant.accessToken
    }
}
