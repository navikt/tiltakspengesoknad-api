package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

class TiltakService(
    applicationConfig: ApplicationConfig,
    private val tiltakspengerTiltakClient: TiltakspengerTiltakClient = TiltakspengerTiltakClient(config = applicationConfig),
) {
    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("tjenestekall")

    suspend fun hentTiltak(subjectToken: String, maskerArrangørnavn: Boolean): List<TiltaksdeltakelseDto> {
        log.info { "Henter tiltak" }
        val result = tiltakspengerTiltakClient.fetchTiltak(subjectToken = subjectToken)
        if (result.isSuccess) {
            log.info { "Henting av tiltak OK" }
            val tiltak = result.getOrNull()
            if (tiltak !== null) {
                return tiltak.toTiltakDto(maskerArrangørnavn).filter {
                    it.erInnenforRelevantTidsrom()
                }
            }
        }
        log.error { "Noe gikk galt under kall til tiltakspenger-tiltak " }
        secureLog.error { "Exception ved kall mot tiltakspenger-tiltak: ${result.exceptionOrNull()}" }
        throw IllegalStateException("Noe gikk galt under kall til tiltakspenger-tiltak")
    }
}
