package no.nav.tiltakspenger.soknad.api.featuretoggling

import io.getunleash.DefaultUnleash
import io.getunleash.util.UnleashConfig
import io.ktor.server.application.ApplicationEnvironment

fun setupUnleash(environment: ApplicationEnvironment): DefaultUnleash {
    val appName = "tiltakspenger-soknad-api"
    val config = UnleashConfig.builder()
        .appName(appName)
        .instanceId(appName)
        .unleashAPI(environment.config.property("unleash.unleash_server_api_url").getString())
        .apiKey(environment.config.property("unleash.unleash_server_api_token").getString())
        .build()
    return DefaultUnleash(config)
}
