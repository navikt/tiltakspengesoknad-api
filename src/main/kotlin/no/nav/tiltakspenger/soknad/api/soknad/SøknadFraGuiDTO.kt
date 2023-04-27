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
    // val oppholderSegUtenforEøs: Boolean, Denne kommer snart...
)

data class RegistrertBarn(
    val ident: String = "Kommer snart",
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val fødselsdato: LocalDate,
    val bostedsland: String,
    // val oppholderSegUtenforEøs: Boolean, Denne kommer snart...
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
)

data class Barnetillegg(
    val søkerOmBarnetillegg: Boolean, // denne skal bort
    val ønskerÅSøkeBarnetilleggForAndreBarn: Boolean?, // denne trenger vi vel ikke
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

data class SøknadFraGuiDTO(
    val kvalifiseringsprogram: Kvalifiseringsprogram,
    val introduksjonsprogram: Introduksjonsprogram,
    val institusjonsopphold: Institusjonsopphold,
    val tiltak: Tiltak,
    val barnetillegg: Barnetillegg,
    val pensjonsordning: Pensjonsordning,
    val etterlønn: Etterlønn,
)
