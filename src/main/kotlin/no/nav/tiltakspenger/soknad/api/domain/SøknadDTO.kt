package no.nav.tiltakspenger.soknad.api.domain

import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.soknad.Alderspensjon
import no.nav.tiltakspenger.soknad.api.soknad.Barnetillegg
import no.nav.tiltakspenger.soknad.api.soknad.Etterlønn
import no.nav.tiltakspenger.soknad.api.soknad.Gjenlevendepensjon
import no.nav.tiltakspenger.soknad.api.soknad.Institusjonsopphold
import no.nav.tiltakspenger.soknad.api.soknad.Introduksjonsprogram
import no.nav.tiltakspenger.soknad.api.soknad.Jobbsjansen
import no.nav.tiltakspenger.soknad.api.soknad.Kvalifiseringsprogram
import no.nav.tiltakspenger.soknad.api.soknad.ManueltRegistrertBarn
import no.nav.tiltakspenger.soknad.api.soknad.Pensjonsordning
import no.nav.tiltakspenger.soknad.api.soknad.Periode
import no.nav.tiltakspenger.soknad.api.soknad.RegistrertBarn
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import no.nav.tiltakspenger.soknad.api.soknad.Supplerendestønadflyktninger
import no.nav.tiltakspenger.soknad.api.soknad.Supplerendestønadover67
import no.nav.tiltakspenger.soknad.api.soknad.Sykepenger
import no.nav.tiltakspenger.soknad.api.soknad.Tiltak
import java.time.LocalDateTime
import java.util.UUID
import no.nav.tiltakspenger.soknad.api.soknad.LønnetArbeid

data class Personopplysninger(
    val ident: String,
    val fornavn: String,
    val etternavn: String,
)

data class SøknadDTO(
    val id: UUID = UUID.randomUUID(),
    val acr: String,
    val versjon: String = "3", // Husk å bumpe denne og map i mottak hvis du gjør endringer på json
    val kvalifiseringsprogram: Kvalifiseringsprogram,
    val introduksjonsprogram: Introduksjonsprogram,
    val institusjonsopphold: Institusjonsopphold,
    val tiltak: Tiltak,
    val vedleggsnavn: List<String>,
    val barnetillegg: Barnetillegg,
    val mottarAndreUtbetalinger: Boolean,
    val sykepenger: Sykepenger,
    val gjenlevendepensjon: Gjenlevendepensjon,
    val alderspensjon: Alderspensjon,
    val supplerendestønadover67: Supplerendestønadover67,
    val supplerendestønadflyktninger: Supplerendestønadflyktninger,
    val pensjonsordning: Pensjonsordning,
    val etterlønn: Etterlønn,
    val lønnetArbeid: LønnetArbeid,
    val jobbsjansen: Jobbsjansen,
    val personopplysninger: Personopplysninger,
    val harBekreftetAlleOpplysninger: Boolean,
    val harBekreftetÅSvareSåGodtManKan: Boolean,
    val innsendingTidspunkt: LocalDateTime,
) {
    companion object {
        fun toDTO(
            req: SpørsmålsbesvarelserDTO,
            fnr: String,
            person: PersonDTO,
            acr: String,
            innsendingTidspunkt: LocalDateTime,
            vedleggsnavn: List<String>,
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
                    arrangør = req.tiltak.arrangør,
                    type = req.tiltak.type,
                    typeNavn = req.tiltak.typeNavn,
                    arenaRegistrertPeriode = req.tiltak.arenaRegistrertPeriode,
                ),
                vedleggsnavn = vedleggsnavn,
                barnetillegg = Barnetillegg(
                    manueltRegistrerteBarnSøktBarnetilleggFor = req.barnetillegg.manueltRegistrerteBarnSøktBarnetilleggFor.map {
                        ManueltRegistrertBarn(
                            fornavn = it.fornavn,
                            mellomnavn = it.mellomnavn,
                            etternavn = it.etternavn,
                            fødselsdato = it.fødselsdato,
                            oppholdInnenforEøs = it.oppholdInnenforEøs,
                        )
                    },
                    registrerteBarnSøktBarnetilleggFor = req.barnetillegg.registrerteBarnSøktBarnetilleggFor.map {
                        RegistrertBarn(
                            fornavn = it.fornavn,
                            mellomnavn = it.mellomnavn,
                            etternavn = it.etternavn,
                            fødselsdato = it.fødselsdato,
                            oppholdInnenforEøs = it.oppholdInnenforEøs,
                        )
                    },
                ), // fylle ut barn fra person her?
                mottarAndreUtbetalinger = req.mottarAndreUtbetalinger,
                pensjonsordning = Pensjonsordning(
                    mottar = req.pensjonsordning.mottar,
                    periode = req.pensjonsordning.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                lønnetArbeid = LønnetArbeid(
                    erILønnetArbeid = req.lønnetArbeid.erILønnetArbeid,
                ),
                etterlønn = Etterlønn(
                    mottar = req.etterlønn.mottar,
                ),
                sykepenger = Sykepenger(
                    mottar = req.sykepenger.mottar,
                    periode = req.sykepenger.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                gjenlevendepensjon = Gjenlevendepensjon(
                    mottar = req.gjenlevendepensjon.mottar,
                    periode = req.gjenlevendepensjon.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                alderspensjon = Alderspensjon(
                    mottar = req.alderspensjon.mottar,
                    fraDato = req.alderspensjon.fraDato,
                ),
                supplerendestønadover67 = Supplerendestønadover67(
                    mottar = req.supplerendestønadover67.mottar,
                    periode = req.supplerendestønadover67.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                supplerendestønadflyktninger = Supplerendestønadflyktninger(
                    mottar = req.supplerendestønadflyktninger.mottar,
                    periode = req.supplerendestønadflyktninger.periode?.let {
                        Periode(
                            fra = it.fra,
                            til = it.til,
                        )
                    },
                ),
                jobbsjansen = Jobbsjansen(
                    mottar = req.jobbsjansen.mottar,
                    periode = req.jobbsjansen.periode?.let {
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
                harBekreftetÅSvareSåGodtManKan = req.harBekreftetÅSvareSåGodtManKan,
                innsendingTidspunkt = innsendingTidspunkt,
            )
        }
    }
}
