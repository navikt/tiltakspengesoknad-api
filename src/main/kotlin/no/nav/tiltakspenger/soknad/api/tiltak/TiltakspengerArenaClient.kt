package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import no.nav.tiltakspenger.soknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspenger.soknad.api.httpClientCIO

class TiltakspengerArenaClient(
    config: ApplicationConfig,
    private val httpClient: HttpClient = httpClientCIO(timeout = 10L),
) {
    private val tiltakspengerArenaEndpoint = config.property("endpoints.tiltakspengerarena").getString()
    private val tiltakspengerArenaAudience = config.property("audience.tiltakspengerarena").getString()
    private val oauth2ClientTokenX = checkNotNull(ClientConfig(config, httpClientCIO()).clients["tokendings"])

    suspend fun fetchTiltak(subjectToken: String): Result<ArenaTiltaksaktivitetResponsDTO> {
        val tokenResponse = oauth2ClientTokenX.tokenExchange(subjectToken, tiltakspengerArenaAudience)
        val token = tokenResponse.accessToken
        return kotlin.runCatching {
            httpClient.get("$tiltakspengerArenaEndpoint/tiltak") {
                accept(ContentType.Application.Json)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
