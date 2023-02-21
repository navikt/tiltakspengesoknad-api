import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspengesoknad.api.BARN_PATH

val LOG = KotlinLogging.logger { }

fun Route.pdlRoutes() {
    val oauth2Client = checkNotNull(ClientConfig(environment.config, defaultHttpClient).clients["issuer1"])

    get(path = BARN_PATH) {
        val principal = call.principal<JWTPrincipal>()
        val søkerId = principal!!.payload.getClaim("pid").asString()
        val token = call.principal<TokenValidationContextPrincipal>().asTokenString()
        val oAuth2Response = oauth2Client.tokenExchange(token, "targetaudience")
    }
}

internal fun TokenValidationContextPrincipal?.asTokenString(): String =
    this?.context?.firstValidToken?.map { it.tokenAsString }?.orElse(null)
        ?: throw RuntimeException("no token found in call context")