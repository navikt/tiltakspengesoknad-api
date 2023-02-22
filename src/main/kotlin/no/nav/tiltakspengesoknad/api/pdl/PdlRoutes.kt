package no.nav.tiltakspengesoknad.api.pdl

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspengesoknad.api.BARN_PATH
import no.nav.tiltakspengesoknad.api.auth.asTokenString
import no.nav.tiltakspengesoknad.api.auth.getClaim
import no.nav.tiltakspengesoknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspengesoknad.api.httpClientCIO

fun Route.pdlRoutes(config: ApplicationConfig) {
    val oauth2Client = checkNotNull(ClientConfig(config, httpClientCIO()).clients["tokendings"])
    val log = KotlinLogging.logger {}

    get(path = BARN_PATH) {
        val pid = call.getClaim("tokendings", "pid")
        val token = call.principal<TokenValidationContextPrincipal>().asTokenString()
        val oAuth2Response = oauth2Client.tokenExchange(token, "")
        call.respondText(status = HttpStatusCode.OK, text = "OK")
    }
}
