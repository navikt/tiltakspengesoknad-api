package no.nav.tiltakspengesoknad.api

import com.natpryce.konfig.*


object Configuration {
  private val devProperties = mapOf(
    "application.httpPort" to 8080.toString()
  )

  private val defaultProperties = ConfigurationMap(devProperties)

  private fun config() = ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding defaultProperties

  fun applicationPort(): Int = config()[Key("application.httpPort", intType)]
}