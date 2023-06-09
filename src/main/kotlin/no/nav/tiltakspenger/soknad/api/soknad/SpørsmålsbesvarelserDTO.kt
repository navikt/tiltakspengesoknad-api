package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.isSameOrAfter
import no.nav.tiltakspenger.soknad.api.isSameOrBefore
import no.nav.tiltakspenger.soknad.api.tiltak.Deltakelsesperiode
import java.time.LocalDate

data class Periode(
    val fra: LocalDate,
    val til: LocalDate,
) {
    fun erGyldig(): Boolean {
        return fra.isSameOrBefore(til)
    }

    fun erInnenfor(periode: Periode): Boolean {
        return fra.isSameOrAfter(periode.fra) &&
            fra.isSameOrBefore(periode.til) &&
            til.isSameOrAfter(periode.fra) &&
            til.isSameOrBefore(periode.til)
    }
}

data class ManueltRegistrertBarn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val fødselsdato: LocalDate,
)

data class RegistrertBarn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val fødselsdato: LocalDate,
)

data class Kvalifiseringsprogram(
    val deltar: Boolean,
    val periode: Periode?,
)

data class Introduksjonsprogram(
    val deltar: Boolean,
    val periode: Periode?,
)

data class Institusjonsopphold(
    val borPåInstitusjon: Boolean,
    val periode: Periode?,
)

data class Tiltak(
    val aktivitetId: String,
    val periode: Periode,
    val arenaRegistrertPeriode: Deltakelsesperiode?,
    val arrangør: String,
    val type: String,
    val typeNavn: String,
) {
    fun harKunFradatoIArena(): Boolean {
        return arenaRegistrertPeriode?.fra != null && arenaRegistrertPeriode.til == null
    }

    fun harFullstendigPeriodeIArena(): Boolean {
        return arenaRegistrertPeriode?.fra != null && arenaRegistrertPeriode.til != null
    }

    fun søktPeriodeErInnenforArenaRegistrertPeriode(): Boolean {
        val fraDatoIArena = arenaRegistrertPeriode?.fra
        val tilDatoIArena = arenaRegistrertPeriode?.til
        if (harKunFradatoIArena()) {
            return periode.fra.isSameOrAfter(fraDatoIArena!!) && periode.til.isSameOrAfter(fraDatoIArena)
        }
        if (harFullstendigPeriodeIArena()) {
            return periode.fra.isSameOrAfter(fraDatoIArena!!) &&
                periode.fra.isSameOrBefore(tilDatoIArena!!) &&
                periode.til.isSameOrAfter(fraDatoIArena) &&
                periode.til.isSameOrBefore(tilDatoIArena)
        }

        // todo: hvordan validere tiltak som ikke har noen periode i Arena?
        return true
    }
}

data class Barnetillegg(
    val manueltRegistrerteBarnSøktBarnetilleggFor: List<ManueltRegistrertBarn>,
    val registrerteBarnSøktBarnetilleggFor: List<RegistrertBarn>,
)

data class Pensjonsordning(
    val mottar: Boolean?,
)

data class Etterlønn(
    val mottar: Boolean?,
)

data class Sykepenger(
    val mottar: Boolean?,
    val periode: Periode?,
)

data class Gjenlevendepensjon(
    val mottar: Boolean?,
    val periode: Periode?,
)

data class Alderspensjon(
    val mottar: Boolean?,
    val fraDato: LocalDate?,
)

data class Supplerendestønadover67(
    val mottar: Boolean?,
    val periode: Periode?,
)

data class Supplerendestønadflyktninger(
    val mottar: Boolean?,
    val periode: Periode?,
)

data class Jobbsjansen(
    val mottar: Boolean?,
    val periode: Periode?,
)

data class SpørsmålsbesvarelserDTO(
    val kvalifiseringsprogram: Kvalifiseringsprogram,
    val introduksjonsprogram: Introduksjonsprogram,
    val institusjonsopphold: Institusjonsopphold,
    val tiltak: Tiltak,
    val barnetillegg: Barnetillegg,
    val mottarAndreUtbetalinger: Boolean,
    val sykepenger: Sykepenger,
    val gjenlevendepensjon: Gjenlevendepensjon,
    val alderspensjon: Alderspensjon,
    val supplerendestønadover67: Supplerendestønadover67,
    val supplerendestønadflyktninger: Supplerendestønadflyktninger,
    val pensjonsordning: Pensjonsordning,
    val etterlønn: Etterlønn,
    val jobbsjansen: Jobbsjansen,
    val harBekreftetAlleOpplysninger: Boolean,
    val harBekreftetÅSvareSåGodtManKan: Boolean,
) {
    fun valider(): List<String> = valider(this)
}
