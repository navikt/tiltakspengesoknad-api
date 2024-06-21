package no.nav.tiltakspenger.soknad.api.extensions

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse

fun OAuth2AccessTokenResponse.getAccessTokenOrThrow() =
    this.accessToken ?: throw IllegalStateException("Responsen fra token-exchange mangler accessToken")
