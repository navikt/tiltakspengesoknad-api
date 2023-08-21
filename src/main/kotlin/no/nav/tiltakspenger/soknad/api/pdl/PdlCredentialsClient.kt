package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.httpClientWithRetry

class PdlCredentialsClient(
    config: ApplicationConfig,
    private val httpClient: HttpClient = httpClientWithRetry(timeout = 10L),
) {
    private val pdlEndpoint = config.property("endpoints.pdl").getString()
    private val pdlScope = config.property("scope.pdl").getString()
    private val oauth2CredentialsClient = checkNotNull(ClientConfig(config, httpClientWithRetry()).clients["azure"])

    suspend fun fetchBarn(ident: String, callId: String): Result<SøkersBarnRespons> {
        val clientCredentialsGrant = oauth2CredentialsClient.clientCredentials(pdlScope)
        val token = clientCredentialsGrant.accessToken
        return kotlin.runCatching {
            httpClient.post(pdlEndpoint) {
                accept(ContentType.Application.Json)
                header("Tema", INDIVIDSTONAD)
                header("Nav-Call-Id", callId)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(hentBarnQuery(ident))
            }.body()
        }
    }
}
