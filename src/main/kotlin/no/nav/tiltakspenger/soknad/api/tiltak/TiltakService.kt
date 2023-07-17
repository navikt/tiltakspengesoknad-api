package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

class TiltakService(
    applicationConfig: ApplicationConfig,
) {
    private val tiltakspengerArenaClient = TiltakspengerArenaClient(config = applicationConfig)
    private val log = KotlinLogging.logger {}

    suspend fun hentTiltak(subjectToken: String): TiltakDto {
        log.info { "Henter tiltak fra Arena" }
        val result = tiltakspengerArenaClient.fetchTiltak(subjectToken = subjectToken)
        if (result.isSuccess) {
            log.info { "Henting av tiltak OK" }
            val tiltak = result.getOrNull()
            if (tiltak !== null) {
                return TiltakDto(
                    tiltak = ArenaTiltakResponse(
                        tiltaksaktiviteter = tiltak.tiltaksaktiviteter,
                        feil = tiltak.feil,
                    ).toTiltakDto().tiltak.filter {
                        it.erInnenforRelevantTidsrom() && it.harRelevantStatus() && it.type.rettPåTiltakspenger
                    },
                )
            }
        }
        log.error { "Noe gikk galt under kall til tiltakspenger-arena" }
        throw IllegalStateException("Noe gikk galt under kall til tiltakspenger-arena")
    }
}
