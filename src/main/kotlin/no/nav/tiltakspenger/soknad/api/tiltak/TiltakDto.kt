package no.nav.tiltakspenger.soknad.api.tiltak

import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import java.time.LocalDate

data class Deltakelsesperiode(
    val fra: LocalDate?,
    val til: LocalDate?,
)

data class TiltaksdeltakelseDto(
    val aktivitetId: String,
    val type: ArenaTiltaksaktivitetResponsDTO.TiltakType,
    val typeNavn: String,
    val arenaRegistrertPeriode: Deltakelsesperiode,
    val arrangør: String,
    // val status: ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType,
) {
    fun erInnenforRelevantTidsrom(): Boolean {
        val datoFor6MånederSiden = LocalDate.now().minusMonths(6)
        val dato2MånederFrem = LocalDate.now().plusMonths(2)

        return if (arenaRegistrertPeriode.fra == null) {
            true
        } else if (arenaRegistrertPeriode.til == null) {
            arenaRegistrertPeriode.fra.isBefore(dato2MånederFrem) && arenaRegistrertPeriode.fra.isAfter(datoFor6MånederSiden)
        } else {
            arenaRegistrertPeriode.fra.isBefore(dato2MånederFrem) && arenaRegistrertPeriode.til.isAfter(datoFor6MånederSiden)
        }
    }

/*    fun harRelevantStatus(): Boolean {
        return status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.AKTUELL ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.JATAKK ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.FULLF ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.DELAVB ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.TILBUD
    }*/
}

data class TiltakDto(
    val tiltak: List<TiltaksdeltakelseDto>,
)

/*data class ArenaTiltakResponse(
    val tiltaksaktiviteter: List<ArenaTiltaksaktivitetResponsDTO.TiltaksaktivitetDTO>? = null,
    val feil: ArenaTiltaksaktivitetResponsDTO.FeilmeldingDTO? = null,
) {
    fun toTiltakDto(maskerArrangørnavn: Boolean): TiltakDto {
        return TiltakDto(
            tiltak = (tiltaksaktiviteter ?: emptyList()).map {
                TiltaksdeltakelseDto(
                    aktivitetId = it.aktivitetId,
                    type = it.tiltakType,
                    typeNavn = it.tiltakType.navn,
                    arenaRegistrertPeriode = Deltakelsesperiode(
                        fra = it.deltakelsePeriode?.fom,
                        til = it.deltakelsePeriode?.tom,
                    ),
                    arrangør = if (maskerArrangørnavn) "" else it.arrangoer ?: "",
                    status = it.deltakerStatusType,
                )
            },
        )
    }
}*/

data class TiltakspengerTiltakResponse(
    val tiltaksaktiviteter: List<TiltakDeltakelseResponse>,
) {
    fun toTiltakDto(maskerArrangørnavn: Boolean): TiltakDto {
        return TiltakDto(
            tiltak = tiltaksaktiviteter.map {
                TiltaksdeltakelseDto(
                    aktivitetId = it.id,
                    type = ArenaTiltaksaktivitetResponsDTO.TiltakType.valueOf(it.gjennomforing.arenaKode),
                    typeNavn = it.gjennomforing.typeNavn,
                    arenaRegistrertPeriode = Deltakelsesperiode(
                        fra = it.startDato,
                        til = it.sluttDato,
                    ),
                    arrangør = if (maskerArrangørnavn) "" else it.gjennomforing.arrangornavn ?: "",
                    // status = ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.valueOf(it.status.name),
                )
            },
        )
    }
}
