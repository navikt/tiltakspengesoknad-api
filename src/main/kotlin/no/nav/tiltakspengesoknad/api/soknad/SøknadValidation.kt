package no.nav.tiltakspengesoknad.api.soknad

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import no.nav.tiltakspengesoknad.api.domain.Søknad

fun RequestValidationConfig.validateSøknad() {
    validate<Søknad> { søknad ->
        if (søknad.deltarIKvp) {
            ValidationResult.Invalid("Test: Søker deltar i KVP")
        } else if (søknad.deltarIIntroprogrammet) {
            ValidationResult.Invalid("Test: Søker deltar i IP")
        } else {
            ValidationResult.Valid
        }
    }
}
