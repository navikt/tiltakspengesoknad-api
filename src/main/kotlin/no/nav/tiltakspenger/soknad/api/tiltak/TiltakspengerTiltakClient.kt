package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO.TiltakDTO
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.extensions.getAccessTokenOrThrow
import no.nav.tiltakspenger.soknad.api.httpClientCIO
import no.nav.tiltakspenger.soknad.api.httpClientWithRetry

class TiltakspengerTiltakClient(
    config: ApplicationConfig,
    private val httpClient: HttpClient = httpClientWithRetry(timeout = 10L),
) {
    private val tiltakspengerTiltakEndpoint = config.property("endpoints.tiltakspengertiltak").getString()
    private val tiltakspengerTiltakAudience = config.property("audience.tiltakspengertiltak").getString()
    private val oauth2ClientTokenX = checkNotNull(ClientConfig(config, httpClientCIO()).clients["tokendings"])

    suspend fun fetchTiltak(subjectToken: String): Result<List<TiltakDTO>> {
        val tokenResponse = oauth2ClientTokenX.tokenExchange(subjectToken, tiltakspengerTiltakAudience)
        val token = tokenResponse.getAccessTokenOrThrow()
        return kotlin.runCatching {
            httpClient.get("$tiltakspengerTiltakEndpoint/tokenx/tiltak") {
                accept(ContentType.Application.Json)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
