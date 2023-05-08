package no.nav.tiltakspenger.soknad.api.soknad
import java.time.LocalDate

data class Periode(
    val fra: LocalDate,
    val til: LocalDate,
)

data class ManueltRegistrertBarn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val fødselsdato: LocalDate,
    val bostedsland: String,
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
    val periode: Periode?,
    val søkerHeleTiltaksperioden: Boolean,
    val arrangør: String,
    val type: String,
)

data class Barnetillegg(
    val søkerOmBarnetillegg: Boolean,
    val ønskerÅSøkeBarnetilleggForAndreBarn: Boolean?,
    val manueltRegistrerteBarnSøktBarnetilleggFor: List<ManueltRegistrertBarn>,
    val registrerteBarnSøktBarnetilleggFor: List<RegistrertBarn>,
)

data class Pensjonsordning(
    val mottarEllerSøktPensjonsordning: Boolean,
    val utbetaler: String?,
    val periode: Periode?,
)

data class Etterlønn(
    val mottarEllerSøktEtterlønn: Boolean,
    val utbetaler: String?,
    val periode: Periode?,
)

data class SpørsmålsbesvarelserDTO(
    val kvalifiseringsprogram: Kvalifiseringsprogram,
    val introduksjonsprogram: Introduksjonsprogram,
    val institusjonsopphold: Institusjonsopphold,
    val tiltak: Tiltak,
    val barnetillegg: Barnetillegg,
    val pensjonsordning: Pensjonsordning,
    val etterlønn: Etterlønn,
    val harBekreftetAlleOpplysninger: Boolean,
) {
    init {
        if (harBekreftetAlleOpplysninger == false) {
            require(harBekreftetAlleOpplysninger == true) { "Bruker må bekrefte å ha oppgitt riktige opplysninger" }
        }
        if (kvalifiseringsprogram.deltar == false) {
            require(kvalifiseringsprogram.periode == null) { "Kvalifisering uten deltagelse kan ikke ha noen periode" }
        } else {
            require(kvalifiseringsprogram.periode != null) { "Kvalifisering med deltagelse må ha periode" }
            require(kvalifiseringsprogram.periode.fra.isBefore(kvalifiseringsprogram.periode.til.plusDays(1))) { "Kvalifisering fra dato må være tidligere eller lik til dato" }
            if (tiltak.periode != null) { // kan denne være null??
                require(!kvalifiseringsprogram.periode.fra.isBefore(tiltak.periode.fra)) { "Kvalifisering fra dato kan ikke være før fra dato på tiltaket" }
                require(!kvalifiseringsprogram.periode.til.isAfter(tiltak.periode.til)) { "Kvalifisering til dato kan ikke være etter til dato på tiltaket" }
            }
        }
        if (introduksjonsprogram.deltar == false) {
            require(introduksjonsprogram.periode == null) { "Introduksjonsprogram uten deltagelse kan ikke ha noen periode" }
        } else {
            require(introduksjonsprogram.periode != null) { "Introduksjonsprogram med deltagelse må ha periode" }
            require(introduksjonsprogram.periode.fra.isBefore(introduksjonsprogram.periode.til.plusDays(1))) { "Introduksjonsprogram fra dato må være tidligere eller lik til dato" }
            if (tiltak.periode != null) { // kan denne være null??
                require(!introduksjonsprogram.periode.fra.isBefore(tiltak.periode.fra)) { "Introduksjonsprogram fra dato kan ikke være før fra dato på tiltaket" }
                require(!introduksjonsprogram.periode.til.isAfter(tiltak.periode.til)) { "Introduksjonsprogram til dato kan ikke være etter til dato på tiltaket" }
            }
        }
    }
}
