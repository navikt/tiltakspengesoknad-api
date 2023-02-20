package no.nav.tiltakspengesoknad.api.auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import no.nav.security.token.support.v2.RequiredClaims
import no.nav.security.token.support.v2.tokenValidationSupport

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")
fun Application.installAuthentication(config: ApplicationConfig) {
    val acceptedIssuer = config.property("security.issuer_name").getString()

    install(Authentication) {
        // Default validation
        tokenValidationSupport(config = config)

        // Only allow token with specific claim and claim value
        tokenValidationSupport(
            name = "userTest",
            config = config,
            requiredClaims = RequiredClaims(
                issuer = acceptedIssuer,
                claimMap = arrayOf("id=123test"),
            ),
        )

        // Only allow token that contains at least one matching claim and claim value
        tokenValidationSupport(
            name = "usersTest",
            config = config,
            requiredClaims = RequiredClaims(
                issuer = acceptedIssuer,
                claimMap = arrayOf("id=123test", "id=234test"),
                combineWithOr = true,
            ),
        )

        tokenValidationSupport(name = "ValidScope", config = config, additionalValidation = { ctx ->
            val scopes = ctx.getClaims(acceptedIssuer)
                ?.getStringClaim("scope")
                ?.split(" ")
                ?: emptyList()

            val allowedScopes = setOf("nav:domain:read", "nav:domain:write")
            scopes.any(allowedScopes::contains)
        })
    }
}
