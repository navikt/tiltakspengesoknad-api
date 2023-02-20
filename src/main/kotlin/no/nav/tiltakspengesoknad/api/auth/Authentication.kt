package no.nav.tiltakspengesoknad.api.auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.config.ApplicationConfig
import no.nav.security.token.support.v2.RequiredClaims
import no.nav.security.token.support.v2.tokenValidationSupport

fun Application.installAuthentication(config: ApplicationConfig) {

    install(Authentication) {
        tokenValidationSupport(
            config = config,
            requiredClaims = RequiredClaims(issuer = "tokendings", claimMap = arrayOf("acr=Level4")),
        )
    }
}
