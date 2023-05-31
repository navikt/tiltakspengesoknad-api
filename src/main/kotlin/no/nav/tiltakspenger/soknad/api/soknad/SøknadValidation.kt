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

    if (søknad.harBekreftetÅSvareSåGodtManKan == false) {
        feilmeldinger.add("Bruker må bekrefte å svare så godt man kan")
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

    if(søknad.mottarAndreUtbetalinger == true) {
        if (søknad.sykepenger.mottar == false) {
            if (søknad.sykepenger.periode != null) {
                feilmeldinger.add("Person som mottar ikke sykepenger kan ikke ha sykepenger periode")
            }
        } else {
            if (søknad.sykepenger.periode == null) {
                feilmeldinger.add("Person som mottar sykepenger må ha sykepenger periode")
            } else {
                if (!søknad.sykepenger.periode.fra.isBefore(
                        søknad.sykepenger.periode.til.plusDays(1),
                    )
                ) {
                    feilmeldinger.add("Sykepenger fra dato må være tidligere eller lik til dato")
                }
                if (søknad.sykepenger.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Sykepenger periode kan ikke være senere enn tiltakets periode")
                }
                if (søknad.sykepenger.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                    feilmeldinger.add("Sykepenger periode kan ikke være tidligere enn tiltakets periode")
                }
            }
        }

        if (søknad.gjenlevendepensjon.mottar == false) {
            if (søknad.gjenlevendepensjon.periode != null) {
                feilmeldinger.add("Person som mottar ikke gjenlevendepensjon kan ikke ha gjenlevendepensjon periode")
            }
        } else {
            if (søknad.gjenlevendepensjon.periode == null) {
                feilmeldinger.add("Person som mottar gjenlevendepensjon må ha gjenlevendepensjon periode")
            } else {
                if (!søknad.gjenlevendepensjon.periode.fra.isBefore(
                        søknad.gjenlevendepensjon.periode.til.plusDays(1),
                    )
                ) {
                    feilmeldinger.add("Gjenlevendepensjon fra dato må være tidligere eller lik til dato")
                }
                if (søknad.gjenlevendepensjon.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Gjenlevendepensjon periode kan ikke være senere enn tiltakets periode")
                }
                if (søknad.gjenlevendepensjon.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                    feilmeldinger.add("Gjenlevendepensjon periode kan ikke være tidligere enn tiltakets periode")
                }
            }
        }

        if (søknad.alderspensjon.mottar == false) {
            if (søknad.alderspensjon.fraDato != null) {
                feilmeldinger.add("Person som mottar ikke alderspensjon kan ikke ha alderspensjon fra dato")
            }
        } else {
            if (søknad.alderspensjon.fraDato == null) {
                feilmeldinger.add("Person som mottar alderspensjon må ha fra dato")
            } else {
                if (søknad.alderspensjon.fraDato.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Alderspensjon fra dato kan ikke være senere enn tiltakets periode")
                }
            }
        }

        if (søknad.supplerendestønadflyktninger.mottar == false) {
            if (søknad.supplerendestønadflyktninger.periode != null) {
                feilmeldinger.add("Person som mottar ikke supplerende stønad for flyktninger kan ikke ha periode")
            }
        } else {
            if (søknad.supplerendestønadflyktninger.periode == null) {
                feilmeldinger.add("Person som mottar supplerende stønad for flyktninger må ha periode til det")
            } else {
                if (!søknad.supplerendestønadflyktninger.periode.fra.isBefore(
                        søknad.supplerendestønadflyktninger.periode.til.plusDays(1),
                    )
                ) {
                    feilmeldinger.add("Fra dato til supplerende stønad for flyktninger må være tidligere eller lik til dato")
                }
                if (søknad.supplerendestønadflyktninger.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Periode til supplerende stønad for flyktninger kan ikke være senere enn tiltakets periode")
                }
                if (søknad.supplerendestønadflyktninger.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                    feilmeldinger.add("Periode til supplerende stønad for flyktninger kan ikke være tidligere enn tiltakets periode")
                }
            }
        }

        if (søknad.supplerendestønadover67.mottar == false) {
            if (søknad.supplerendestønadover67.periode != null) {
                feilmeldinger.add("Person som mottar ikke supplerende stønad for personer over 67 år, kan ikke ha periode til det")
            }
        } else {
            if (søknad.supplerendestønadover67.periode == null) {
                feilmeldinger.add("Person som mottar supplerende stønad for personer over 67 år, må ha periode til det")
            } else {
                if (!søknad.supplerendestønadover67.periode.fra.isBefore(
                        søknad.supplerendestønadover67.periode.til.plusDays(1),
                    )
                ) {
                    feilmeldinger.add("Fra dato til supplerende stønad for personer over 67 år må være tidligere eller lik til dato")
                }
                if (søknad.supplerendestønadover67.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Periode til supplerende stønad for personer over 67 år kan ikke være senere enn tiltakets periode")
                }
                if (søknad.supplerendestønadover67.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                    feilmeldinger.add("Periode til supplerende stønad for personer over 67 år kan ikke være tidligere enn tiltakets periode")
                }
            }
        }

        if (søknad.jobbsjansen.mottar == false) {
            if (søknad.jobbsjansen.periode != null) {
                feilmeldinger.add("Person som mottar ikke stønad til jobbsjansen, kan ikke ha periode til det")
            }
        } else {
            if (søknad.jobbsjansen.periode == null) {
                feilmeldinger.add("Person som mottar stønad til jobbsjansen, må ha periode til det")
            } else {
                if (!søknad.jobbsjansen.periode.fra.isBefore(
                        søknad.jobbsjansen.periode.til.plusDays(1),
                    )
                ) {
                    feilmeldinger.add("Fra dato til jobbsjansen stønad må være tidligere eller lik til dato")
                }
                if (søknad.jobbsjansen.periode.fra.isAfter(søknad.tiltak.periode.til)) {
                    feilmeldinger.add("Periode til jobbsjansen stønad kan ikke være senere enn tiltakets periode")
                }
                if (søknad.jobbsjansen.periode.til.isBefore(søknad.tiltak.periode.fra)) {
                    feilmeldinger.add("Periode til jobbsjansen stønad kan ikke være tidligere enn tiltakets periode")
                }
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

    if (søknad.barnetillegg.søkerOmBarnetillegg) {
        if (søknad.barnetillegg.ønskerÅSøkeBarnetilleggForAndreBarn == null) {
            feilmeldinger.add("Hvis man søker om barnetillegg må man velge om man skal søke for andre barn eller ikke")
        } else {
            if (søknad.barnetillegg.ønskerÅSøkeBarnetilleggForAndreBarn == true) {
                if (søknad.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.isEmpty()) {
                    feilmeldinger.add("Har sagt at man skal søke barnetillegg for andre barn, men ikke sendt inn noen barn")
                }
                søknad.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.map {
                    if (it.fødselsdato.isBefore(søknad.tiltak.periode.fra.minusYears(16))) {
                        feilmeldinger.add("Kan ikke søke for manuelle barn som er mer enn 16 år når tiltaket starter")
                    }
                }
            }
        }
        søknad.barnetillegg.registrerteBarnSøktBarnetilleggFor.map {
            if (it.fødselsdato.isBefore(søknad.tiltak.periode.fra.minusYears(16))) {
                feilmeldinger.add("Kan ikke søke for registrerte barn som er mer enn 16 år når tiltaket starter")
            }
        }
    } else {
        if (søknad.barnetillegg.ønskerÅSøkeBarnetilleggForAndreBarn != null) {
            feilmeldinger.add("Kan ikke søke for andre barn når man ikke har søkt om barnetillegg")
        }
        if (søknad.barnetillegg.registrerteBarnSøktBarnetilleggFor.isNotEmpty()) {
            feilmeldinger.add("Kan ikke sende inn registrerte barn når man ikke har søkt om barnetillegg")
        }
        if (søknad.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.isNotEmpty()) {
            feilmeldinger.add("Kan ikke sende inn manuelle barn når man ikke har søkt om barnetillegg")
        }
    }

    if (søknad.tiltak.periode.fra.isAfter(søknad.tiltak.periode.til)) {
        feilmeldinger.add("Tiltak fra dato kan ikke være etter til dato")
    }

    return feilmeldinger
}
