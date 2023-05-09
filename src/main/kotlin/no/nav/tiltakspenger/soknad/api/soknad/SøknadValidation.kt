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

    if (søknad.institusjonsopphold.borPåInstitusjon == false) {
        if (søknad.institusjonsopphold.periode != null) {
            feilmeldinger.add("Institusjonsopphold uten deltagelse kan ikke ha noen periode")
        }
    } else {
        if (søknad.institusjonsopphold.periode == null) {
            feilmeldinger.add("Institusjonsopphold med deltagelse må ha periode")
        } else {
            if (!søknad.institusjonsopphold.periode.fra.isBefore(
                    søknad.institusjonsopphold.periode.til.plusDays(1),
                )
            ) {
                feilmeldinger.add("Institusjonsopphold fra dato må være tidligere eller lik til dato")
            }
            if (søknad.institusjonsopphold.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                feilmeldinger.add("Institusjonsopphold periode kan ikke være senere enn tiltakets periode")
            }
            if (søknad.institusjonsopphold.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                feilmeldinger.add("Institusjonsopphold periode kan ikke være tidligere enn tiltakets periode")
            }
        }
    }

    if (søknad.pensjonsordning.mottarEllerSøktPensjonsordning == false) {
        if (søknad.pensjonsordning.periode != null) {
            feilmeldinger.add("En som ikke mottar pensjon kan ikke ha periode")
        }
        if (søknad.pensjonsordning.utbetaler != null) {
            feilmeldinger.add("En som ikke mottar pensjon kan ikke ha en utbetaler")
        }
    } else {
        if (søknad.pensjonsordning.utbetaler == null) {
            feilmeldinger.add("En som mottar pensjon må ha en utbetaler")
        }
        if (søknad.pensjonsordning.periode == null) {
            feilmeldinger.add("En som mottar pensjon må ha periode")
        } else {
            if (!søknad.pensjonsordning.periode.fra.isBefore(
                    søknad.pensjonsordning.periode.til.plusDays(1),
                )
            ) {
                feilmeldinger.add("Pensjonsordning fra dato må være tidligere eller lik til dato")
            }
            if (søknad.pensjonsordning.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                feilmeldinger.add("Pensjonsordning periode kan ikke være senere enn tiltakets periode")
            }
            if (søknad.pensjonsordning.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                feilmeldinger.add("Pensjonsordning periode kan ikke være tidligere enn tiltakets periode")
            }
        }
    }

    if (søknad.etterlønn.mottarEllerSøktEtterlønn == false) {
        if (søknad.etterlønn.periode != null) {
            feilmeldinger.add("En som ikke mottar etterlønn kan ikke ha periode")
        }
        if (søknad.etterlønn.utbetaler != null) {
            feilmeldinger.add("En som ikke mottar etterlønn kan ikke ha en utbetaler")
        }
    } else {
        if (søknad.etterlønn.utbetaler == null) {
            feilmeldinger.add("En som mottar etterlønn må ha en utbetaler")
        }
        if (søknad.etterlønn.periode == null) {
            feilmeldinger.add("En som mottar etterlønn må ha periode")
        } else {
            if (!søknad.etterlønn.periode.fra.isBefore(
                    søknad.etterlønn.periode.til.plusDays(1),
                )
            ) {
                feilmeldinger.add("Etterlønn fra dato må være tidligere eller lik til dato")
            }
            if (søknad.etterlønn.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                feilmeldinger.add("Etterlønn periode kan ikke være senere enn tiltakets periode")
            }
            if (søknad.etterlønn.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                feilmeldinger.add("Etterlønn periode kan ikke være tidligere enn tiltakets periode")
            }
        }
    }

    return feilmeldinger
}
