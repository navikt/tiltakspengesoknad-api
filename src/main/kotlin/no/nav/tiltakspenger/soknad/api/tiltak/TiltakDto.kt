package no.nav.tiltakspenger.soknad.api.tiltak

import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import java.time.LocalDate

data class Deltakelsesperiode(
    val fom: LocalDate?,
    val tom: LocalDate?,
)

data class TiltaksdeltakelseDto(
    val aktivitetId: String,
    val type: ArenaTiltaksaktivitetResponsDTO.TiltakType,
    val deltakelsePeriode: Deltakelsesperiode,
    val arrangør: String,
    val status: ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType,
) {
    fun erInnenforRelevantTidsrom(): Boolean {
        val datoFor6MånederSiden = LocalDate.now().minusMonths(6)
        val dato2MånederFrem = LocalDate.now().plusMonths(2)

        return if (deltakelsePeriode.fom == null) {
            true
        } else if (deltakelsePeriode.tom == null) {
            deltakelsePeriode.fom.isBefore(dato2MånederFrem) && deltakelsePeriode.fom.isAfter(datoFor6MånederSiden)
        } else {
            deltakelsePeriode.fom.isBefore(dato2MånederFrem) && deltakelsePeriode.tom.isAfter(datoFor6MånederSiden)
        }
    }

    fun harRelevantStatus(): Boolean {
        return status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.AKTUELL ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.JATAKK ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.FULLF ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.DELAVB
    }
}

data class TiltakDto(
    val tiltak: List<TiltaksdeltakelseDto>,
)

data class ArenaTiltakResponse(
    val tiltaksaktiviteter: List<ArenaTiltaksaktivitetResponsDTO.TiltaksaktivitetDTO>? = null,
    val feil: ArenaTiltaksaktivitetResponsDTO.FeilmeldingDTO? = null,
) {
    fun toTiltakDto(): TiltakDto {
        return TiltakDto(
            tiltak = (tiltaksaktiviteter ?: emptyList()).map {
                TiltaksdeltakelseDto(
                    aktivitetId = it.aktivitetId,
                    type = it.tiltakType,
                    deltakelsePeriode = Deltakelsesperiode(
                        fom = it.deltakelsePeriode?.fom,
                        tom = it.deltakelsePeriode?.tom,
                    ),
                    arrangør = it.arrangoer ?: "",
                    status = it.deltakerStatusType,
                )
            },
        )
    }
}
