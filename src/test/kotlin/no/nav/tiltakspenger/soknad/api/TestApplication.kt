package no.nav.tiltakspenger.soknad.api

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.metrics.MetricsCollector
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.tiltak.TiltakService

fun ApplicationTestBuilder.configureTestApplication(
    pdlService: PdlService = mockk(),
    søknadService: SøknadService = mockk(),
    tiltakService: TiltakService = mockk(),
    avService: AvService = mockk(),
    metricsCollector: MetricsCollector = mockk(relaxed = true),
) {
    environment {
        config = ApplicationConfig("application.test.conf")
    }

    application {
        installAuthentication()
        setupRouting(
            pdlService = pdlService,
            søknadService = søknadService,
            tiltakService = tiltakService,
            avService = avService,
            metricsCollector = metricsCollector,
        )
        installJacksonFeature()
    }
}
