package no.nav.tiltakspengesoknad.api.pdl

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspengesoknad.api.PERSONALIA_PATH
import no.nav.tiltakspengesoknad.api.auth.asTokenString
import no.nav.tiltakspengesoknad.api.auth.getClaim
import no.nav.tiltakspengesoknad.api.auth.oauth.ClientConfig
import no.nav.tiltakspengesoknad.api.httpClientCIO

fun Route.pdlRoutes(config: ApplicationConfig) {
    val oauth2ClientTokenX = checkNotNull(ClientConfig(config, httpClientCIO()).clients["tokendings"])
//    val oauth2ClientClientCredentials = checkNotNull(ClientConfig(config, httpClientCIO()).clients["azure"])

    val log = KotlinLogging.logger {}
    val secureLog = KotlinLogging.logger("tjenestekall")

    get(path = PERSONALIA_PATH) {
        val pdlUrl = config.property("endpoints.pdl").getString()
        val audience = config.property("audience.pdl").getString()
        val pid = call.getClaim("tokendings", "pid")
        val token = call.principal<TokenValidationContextPrincipal>().asTokenString()
        val tokenxResponse = oauth2ClientTokenX.tokenExchange(token, audience)
        val pdlTokenXClient = PdlClient(endpoint = pdlUrl, token = tokenxResponse.accessToken)
        pdlTokenXClient.fetchPerson(pid!!).onSuccess {
            try {
                val person = it.toPerson()
                call.respond(message = person, status = HttpStatusCode.OK)
            } catch (e: Exception) {
                secureLog.error { e }
                call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error")
            }
        }.onFailure {
            secureLog.error { it }
            call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error")
        }
//        val scope = config.property("scope.pdl").getString()
//        val clientCredentialsGrant = oauth2ClientClientCredentials.clientCredentials(scope)
        call.respondText(status = HttpStatusCode.OK, text = "OK")
    }
}
