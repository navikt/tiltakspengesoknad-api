package no.nav.tiltakspenger.soknad.api

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.soknad.api.auth.installAuthentication
import no.nav.tiltakspenger.soknad.api.pdl.PdlService

fun ApplicationTestBuilder.configureTestApplication(pdlService: PdlService = mockk()) {
    environment {
        config = ApplicationConfig("application.test.conf")
    }

    application {
        installAuthentication()
        setupRouting(pdlService = pdlService)
        installJacksonFeature()
    }
}
