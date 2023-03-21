package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO

class TiltakService(
    applicationConfig: ApplicationConfig,
) {
    private val tiltakspengerArenaClient = TiltakspengerArenaClient(config = applicationConfig)

    suspend fun hentTiltak(subjectToken: String): ArenaTiltaksaktivitetResponsDTO {
        val result = tiltakspengerArenaClient.fetchTiltak(subjectToken = subjectToken)
        if (result.isSuccess) {
            val tiltak = result.getOrNull()
            if (tiltak !== null) {
                return tiltak
            }
        }

        throw IllegalStateException("Noe gikk galt under kall til tiltakspenger-arena")
    }
}
