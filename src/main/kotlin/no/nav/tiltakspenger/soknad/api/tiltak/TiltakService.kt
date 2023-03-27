package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig

class TiltakService(
    applicationConfig: ApplicationConfig,
) {
    private val tiltakspengerArenaClient = TiltakspengerArenaClient(config = applicationConfig)

    suspend fun hentTiltak(subjectToken: String): TiltakDto {
        val result = tiltakspengerArenaClient.fetchTiltak(subjectToken = subjectToken)
        if (result.isSuccess) {
            val tiltak = result.getOrNull()
            if (tiltak !== null) {
                return TiltakDto(
                    tiltak = ArenaTiltakResponse(
                        tiltaksaktiviteter = tiltak.tiltaksaktiviteter,
                        feil = tiltak.feil,
                    ).toTiltakDto().tiltak.filter {
                        it.erInnenforRelevantTidsrom() && it.harRelevantStatus()
                    },
                )
            }
        }
        throw IllegalStateException("Noe gikk galt under kall til tiltakspenger-arena")
    }
}
