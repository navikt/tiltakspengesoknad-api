package no.nav.tiltakspenger.soknad.api.featuretoggling

import io.getunleash.DefaultUnleash
import io.getunleash.util.UnleashConfig
import io.ktor.server.application.ApplicationEnvironment

fun setupUnleash(environment: ApplicationEnvironment): DefaultUnleash {
    val appName = "tiltakspenger-soknad-api"
    val unleashApiUrl = "${environment.config.property("unleash.unleash_server_api_url").getString()}/api"
    val unleashApiKey = environment.config.property("unleash.unleash_server_api_token").getString()
    val config = UnleashConfig.builder()
        .appName(appName)
        .environment("development")
        .instanceId(appName)
        .unleashAPI(unleashApiUrl)
        .apiKey(unleashApiKey)
        .build()
    return DefaultUnleash(config)
}
