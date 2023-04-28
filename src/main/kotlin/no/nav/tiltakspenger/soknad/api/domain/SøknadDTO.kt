package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRequest
import java.time.LocalDate
import java.util.UUID

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

data class Personopplysninger(
    val ident: String,
    val fornavn: String,
    val etternavn: String,
)

data class SøknadDTO(
    val id: UUID = UUID.randomUUID(),
    val acr: String,
    val kvalifiseringsprogram: Kvalifiseringsprogram,
    val introduksjonsprogram: Introduksjonsprogram,
    val institusjonsopphold: Institusjonsopphold,
    val tiltak: Tiltak,
    val barnetillegg: Barnetillegg,
    val pensjonsordning: Pensjonsordning,
    val etterlønn: Etterlønn,
    val personopplysninger: Personopplysninger,
) {
    companion object {
        fun toDTO(req: SøknadRequest, fnr: String, person: PersonDTO, acr: String): SøknadDTO {
            return SøknadDTO(
                kvalifiseringsprogram = Kvalifiseringsprogram(
                    deltar = req.kvalifiseringsprogram.deltar,
                    periode = req.kvalifiseringsprogram.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                introduksjonsprogram = Introduksjonsprogram(
                    deltar = req.introduksjonsprogram.deltar,
                    periode = req.introduksjonsprogram.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                institusjonsopphold = Institusjonsopphold(
                    borPåInstitusjon = req.institusjonsopphold.borPåInstitusjon,
                    periode = req.institusjonsopphold.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                tiltak = Tiltak(
                    aktivitetId = req.tiltak.aktivitetId,
                    periode = req.tiltak.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                    søkerHeleTiltaksperioden = req.tiltak.søkerHeleTiltaksperioden,
                ),
                barnetillegg = Barnetillegg(
                    søkerOmBarnetillegg = req.barnetillegg.søkerOmBarnetillegg,
                    ønskerÅSøkeBarnetilleggForAndreBarn = req.barnetillegg.ønskerÅSøkeBarnetilleggForAndreBarn,
                    manueltRegistrerteBarnSøktBarnetilleggFor = req.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.map {
                        ManueltRegistrertBarn(
                            fornavn = it.fornavn,
                            mellomnavn = it.mellomnavn,
                            etternavn = it.etternavn,
                            fødselsdato = it.fødselsdato,
                            bostedsland = it.bostedsland,
                        )
                    },
                    registrerteBarnSøktBarnetilleggFor = req.barnetillegg.registrerteBarnSøktBarnetilleggFor.map {
                        RegistrertBarn(
                            fornavn = it.fornavn,
                            mellomnavn = it.mellomnavn,
                            etternavn = it.etternavn,
                            fødselsdato = it.fødselsdato,
                        )
                    },
                ), // fylle ut barn fra person her?
                pensjonsordning = Pensjonsordning(
                    mottarEllerSøktPensjonsordning = req.pensjonsordning.mottarEllerSøktPensjonsordning,
                    utbetaler = req.pensjonsordning.utbetaler,
                    periode = req.pensjonsordning.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                etterlønn = Etterlønn(
                    mottarEllerSøktEtterlønn = req.etterlønn.mottarEllerSøktEtterlønn,
                    utbetaler = req.etterlønn.utbetaler,
                    periode = req.etterlønn.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                personopplysninger = Personopplysninger(
                    ident = fnr,
                    fornavn = person.fornavn,
                    etternavn = person.etternavn,
                ),
                acr = acr,
            )
        }
    }
}
