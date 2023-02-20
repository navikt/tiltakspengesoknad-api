package no.nav.tiltakspengesoknad.api

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object Configuration {
    private val devProperties = mapOf(
        "logback.configurationFile" to "egenLogback.xml",
        "application.httpPort" to 8082.toString(),
    )

    private val localProperties = ConfigurationMap(
        mapOf(
            "logback.configurationFile" to "logback.local.xml",
            "TOKEN_X_WELL_KNOWN_URL"
                to "TOKEN_X_WELL_KNOWN_URL",
            "TOKEN_X_CLIENT_ID" to "TOKEN_X_CLIENT_ID",
            "TOKEN_X_PRIVATE_JWK" to "TOKEN_X_PRIVATE_JWK",
        ),
    )

    private val authProperties = mapOf(
        "TOKEN_X_WELL_KNOWN_URL"
            to System.getenv("TOKEN_X_WELL_KNOWN_URL"),
        "TOKEN_X_CLIENT_ID" to System.getenv("TOKEN_X_CLIENT_ID"),
        "TOKEN_X_PRIVATE_JWK" to System.getenv("TOKEN_X_PRIVATE_JWK"),
    )

    private val defaultProperties = ConfigurationMap(devProperties + authProperties)

    private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding defaultProperties

        "prod-gcp" ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding defaultProperties

        else -> {
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties overriding defaultProperties
        }
    }

    fun applicationPort(): Int = config()[Key("application.httpPort", intType)]

    data class TokenXConfig(
        val wellKnownUrl: String = config()[Key("TOKEN_X_WELL_KNOWN_URL", stringType)],
        val clientId: String = config()[Key("TOKEN_X_CLIENT_ID", stringType)],
        val privateJwk: String = config()[Key("TOKEN_X_PRIVATE_JWK", stringType)],
        val leeway: Long = 1000,
    )
}
