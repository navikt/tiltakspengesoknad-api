package no.nav.tiltakspenger.soknad.api

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import no.nav.security.token.support.v3.TokenValidationContextPrincipal
import no.nav.tiltakspenger.soknad.api.auth.asTokenString
import no.nav.tiltakspenger.soknad.api.auth.getClaim
import java.time.LocalDate

fun ApplicationCall.fødselsnummer(): String? {
    return this.getClaim("tokendings", "pid")
}

fun ApplicationCall.acr(): String? {
    return this.getClaim("tokendings", "acr")
}

fun ApplicationCall.token(): String {
    return this.principal<TokenValidationContextPrincipal>().asTokenString()
}

fun LocalDate.isSameOrAfter(otherDate: LocalDate): Boolean {
    return this.isEqual(otherDate) || this.isAfter(otherDate)
}

fun LocalDate.isSameOrBefore(otherDate: LocalDate): Boolean {
    return this.isEqual(otherDate) || this.isBefore(otherDate)
}
