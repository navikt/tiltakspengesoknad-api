package no.nav.tiltakspenger.soknad.api

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspenger.soknad.api.auth.asTokenString
import no.nav.tiltakspenger.soknad.api.auth.getClaim

fun ApplicationCall.fødselsnummer(): String? {
    return this.getClaim("tokendings", "pid")
}

fun ApplicationCall.acr(): String? {
    return this.getClaim("tokendings", "acr")
}

fun ApplicationCall.token(): String {
    return this.principal<TokenValidationContextPrincipal>().asTokenString()
}
