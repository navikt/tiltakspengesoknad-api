package no.nav.tiltakspenger.soknad.api.saksbehandlingApi

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.libs.soknad.SøknadDTO
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.auth.oauth.GrantRequest
import no.nav.tiltakspenger.soknad.api.httpClientCIO
import no.nav.tiltakspenger.soknad.api.httpClientWithRetry

class SaksbehandlingApiKlient(
    config: ApplicationConfig,
    private val httpClient: HttpClient = httpClientWithRetry(timeout = 10L),
) {
    private val endpoint = config.property("endpoints.tiltakspengervedtak").getString()
    private val scope = config.property("scope.vedtak").getString()
    private val oauth2Client = checkNotNull(ClientConfig(config, httpClientCIO()).clients["azure"])

    suspend fun sendSøknad(søknadDTO: SøknadDTO, correlationId: CorrelationId) {
        val grantRequest = GrantRequest.clientCredentials(scope)
        val token =
            oauth2Client.accessToken(grantRequest).accessToken ?: throw RuntimeException("Failed to get access token")
        val httpResponse = httpClient.preparePost("$endpoint/soknad") {
            header("Nav-Call-Id", correlationId.toString())
            bearerAuth(token)
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(søknadDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from saksbehandling-api")
        }
    }
}
