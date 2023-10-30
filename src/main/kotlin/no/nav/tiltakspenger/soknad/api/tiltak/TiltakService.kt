package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

class TiltakService(
    applicationConfig: ApplicationConfig,
    private val tiltakspengerTiltakClient: TiltakspengerTiltakClient = TiltakspengerTiltakClient(config = applicationConfig),
) {
    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("tjenestekall")

    suspend fun hentTiltak(subjectToken: String, maskerArrangørnavn: Boolean): TiltakDto {
        log.info { "Henter tiltak fra Arena" }
        val result = tiltakspengerTiltakClient.fetchTiltak(subjectToken = subjectToken)
        if (result.isSuccess) {
            log.info { "Henting av tiltak OK" }
            val tiltak = result.getOrNull()
            if (tiltak !== null) {
                return TiltakDto(
                    tiltak = TiltakspengerTiltakResponse(
                        tiltaksaktiviteter = tiltak,
                    ).toTiltakDto(maskerArrangørnavn).tiltak.filter {
                        it.erInnenforRelevantTidsrom() // && it.harRelevantStatus() && it.type.rettPåTiltakspenger - dette filtreres allerede i den nye tjenesten
                    },
                )
            }
        }
        log.error { "Noe gikk galt under kall til tiltakspenger-tiltak " }
        secureLog.error { "Exception ved kall mot tiltakspenger-tiltak: ${result.exceptionOrNull()}" }
        throw IllegalStateException("Noe gikk galt under kall til tiltakspenger-tiltak")
    }
}
