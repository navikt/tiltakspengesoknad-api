package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.soknad.Barnetillegg
import no.nav.tiltakspenger.soknad.api.soknad.Etterlønn
import no.nav.tiltakspenger.soknad.api.soknad.Institusjonsopphold
import no.nav.tiltakspenger.soknad.api.soknad.Introduksjonsprogram
import no.nav.tiltakspenger.soknad.api.soknad.Kvalifiseringsprogram
import no.nav.tiltakspenger.soknad.api.soknad.ManueltRegistrertBarn
import no.nav.tiltakspenger.soknad.api.soknad.Pensjonsordning
import no.nav.tiltakspenger.soknad.api.soknad.Periode
import no.nav.tiltakspenger.soknad.api.soknad.RegistrertBarn
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import no.nav.tiltakspenger.soknad.api.soknad.Tiltak
import java.time.LocalDateTime
import java.util.UUID

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
    val harBekreftetAlleOpplysninger: Boolean,
    val innsendingTidspunkt: LocalDateTime,
) {
    companion object {
        fun toDTO(
            req: SpørsmålsbesvarelserDTO,
            fnr: String,
            person: PersonDTO,
            acr: String,
            innsendingTidspunkt: LocalDateTime,
        ): SøknadDTO {
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
                    periode = req.tiltak.periode.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                    søkerHeleTiltaksperioden = req.tiltak.søkerHeleTiltaksperioden,
                    arrangør = req.tiltak.arrangør,
                    type = req.tiltak.type,
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
                harBekreftetAlleOpplysninger = req.harBekreftetAlleOpplysninger,
                innsendingTidspunkt = innsendingTidspunkt,
            )
        }
    }
}
