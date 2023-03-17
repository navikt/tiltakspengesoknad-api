package no.nav.tiltakspenger.soknad.api.joark

import io.ktor.server.config.ApplicationConfig

interface TokenService {
    suspend fun getToken(config: ApplicationConfig): String
}
