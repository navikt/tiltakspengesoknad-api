package no.nav.tiltakspengesoknad.api

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding

object Configuration {
    private val devProperties = mapOf(
        "application.httpPort" to 8080.toString(),
    )

    private val defaultProperties = ConfigurationMap(devProperties)

    private fun config() = ConfigurationProperties.systemProperties() overriding
        EnvironmentVariables overriding
            defaultProperties

    fun applicationPort(): Int = config()[Key("application.httpPort", intType)]
}
