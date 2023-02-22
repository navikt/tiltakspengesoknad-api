package no.nav.tiltakspengesoknad.api.pdl

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authentication
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspengesoknad.api.BARN_PATH
import no.nav.tiltakspengesoknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspengesoknad.api.httpClientCIO

fun Route.pdlRoutes(config: ApplicationConfig) {
    val oauth2Client = checkNotNull(ClientConfig(config, httpClientCIO()).clients["tokendings"])
    val log = KotlinLogging.logger {}

    get(path = BARN_PATH) {
        val url = config.property("endpoints.pdl").getString()
        val audience = config.property("audience.pdl").getString()
        val pid = call.getClaim("tokendings", "pid")
        val token = call.principal<TokenValidationContextPrincipal>().asTokenString()
        val oAuth2Response = oauth2Client.tokenExchange(token, audience)
        call.respondText(status = HttpStatusCode.OK, text = "OK")
    }
}

fun ApplicationCall.getClaim(issuer: String, name: String): String? =
    this.authentication.principal<TokenValidationContextPrincipal>()
        ?.context
        ?.getClaims(issuer)
        ?.getStringClaim(name)

internal fun TokenValidationContextPrincipal?.asTokenString(): String =
    this?.context?.firstValidToken?.map { it.tokenAsString }?.orElse(null)
        ?: throw RuntimeException("no token found in call context")
