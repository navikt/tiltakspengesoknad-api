package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun RequestValidationConfig.validateSøknad() {
    validate<SpørsmålsbesvarelserDTO> { søknad ->
        val feilmeldinger = valider(søknad)

        if (feilmeldinger.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(feilmeldinger)
        }
    }
}

fun SpørsmålsbesvarelserDTO.validerRequest(): SpørsmålsbesvarelserDTO {
    val feilmeldinger = valider(this)
    if (feilmeldinger.isNotEmpty()) {
        throw RequestValidationException(this, feilmeldinger)
    }
    return this
}

fun valider(søknad: SpørsmålsbesvarelserDTO): List<String> {
    val feilmeldinger = mutableListOf<String>()

    if (søknad.harBekreftetAlleOpplysninger == false) {
        feilmeldinger.add("Bruker må bekrefte å ha oppgitt riktige opplysninger")
    }

    if (søknad.kvalifiseringsprogram.deltar == false) {
        if (søknad.kvalifiseringsprogram.periode != null) {
            feilmeldinger.add("Kvalifisering uten deltagelse kan ikke ha noen periode")
        }
    } else {
        if (søknad.kvalifiseringsprogram.periode == null) {
            feilmeldinger.add("Kvalifisering med deltagelse må ha periode")
        } else {
            if (!søknad.kvalifiseringsprogram.periode.fra.isBefore(
                    søknad.kvalifiseringsprogram.periode.til.plusDays(1),
                )
            ) {
                feilmeldinger.add("Kvalifisering fra dato må være tidligere eller lik til dato")
            }
            if (søknad.kvalifiseringsprogram.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                feilmeldinger.add("Kvalifisering periode kan ikke være senere enn tiltakets periode")
            }
            if (søknad.kvalifiseringsprogram.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                feilmeldinger.add("Kvalifisering periode kan ikke være tidligere enn tiltakets periode")
            }
        }
    }

    if (søknad.introduksjonsprogram.deltar == false) {
        if (søknad.introduksjonsprogram.periode != null) {
            feilmeldinger.add("Introduksjonsprogram uten deltagelse kan ikke ha noen periode")
        }
    } else {
        if (søknad.introduksjonsprogram.periode == null) {
            feilmeldinger.add("Introduksjonsprogram med deltagelse må ha periode")
        } else {
            if (!søknad.introduksjonsprogram.periode.fra.isBefore(
                    søknad.introduksjonsprogram.periode.til.plusDays(1),
                )
            ) {
                feilmeldinger.add("Introduksjonsprogram fra dato må være tidligere eller lik til dato")
            }
            if (søknad.introduksjonsprogram.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                feilmeldinger.add("Introduksjonsprogram periode kan ikke være senere enn tiltakets periode")
            }
            if (søknad.introduksjonsprogram.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                feilmeldinger.add("Introduksjonsprogram periode kan ikke være tidligere enn tiltakets periode")
            }
        }
    }

    return feilmeldinger
}
