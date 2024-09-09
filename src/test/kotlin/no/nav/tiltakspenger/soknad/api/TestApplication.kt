package no.nav.tiltakspenger.soknad.api

import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.soknad.api.antivirus.AvService
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.metrics.MetricsCollector
import no.nav.tiltakspenger.soknad.api.pdl.PdlService
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepo
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.tiltak.TiltakService
import java.util.UUID.randomUUID

fun ApplicationTestBuilder.configureTestApplication(
    pdlService: PdlService = mockk(),
    søknadService: SøknadService = mockk(),
    tiltakService: TiltakService = mockk(),
    søknadRepo: SøknadRepo = mockk(),
    avService: AvService = mockk(),
    metricsCollector: MetricsCollector = mockk(relaxed = true),
) {
    environment {
        config = ApplicationConfig("application.test.conf")
    }

    application {
        install(CallId) {
            generate { randomUUID().toString() }
        }
        install(CallLogging) {
            callIdMdc("call-id")
        }
        installAuthentication()
        setupRouting(
            pdlService = pdlService,
            søknadService = søknadService,
            tiltakService = tiltakService,
            avService = avService,
            metricsCollector = metricsCollector,
            søknadRepo = søknadRepo,
        )
        installJacksonFeature()
    }
}
