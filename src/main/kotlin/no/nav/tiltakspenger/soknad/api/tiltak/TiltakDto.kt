package no.nav.tiltakspenger.soknad.api.tiltak

import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import java.time.LocalDate

data class Deltakelsesperiode(
    val fom: LocalDate?,
    val tom: LocalDate?,
)

data class TiltaksdeltakelseDto(
    val type: ArenaTiltaksaktivitetResponsDTO.TiltakType,
    val deltakelsePeriode: Deltakelsesperiode,
    val arrangør: String,
) {
    fun erInnenfor6Måneder(): Boolean {
        val datoFor6MånederSiden = LocalDate.now().minusMonths(6)
        val dato6MånederFrem = LocalDate.now().plusMonths(6)

        return if (deltakelsePeriode.fom == null) {
            true
        } else if (deltakelsePeriode.tom == null) {
            deltakelsePeriode.fom.isAfter(datoFor6MånederSiden)
        } else {
            deltakelsePeriode.fom.isAfter(datoFor6MånederSiden) && deltakelsePeriode.tom.isBefore(dato6MånederFrem)
        }
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
                    type = it.tiltakType,
                    deltakelsePeriode = Deltakelsesperiode(
                        fom = it.deltakelsePeriode?.fom,
                        tom = it.deltakelsePeriode?.tom,
                    ),
                    arrangør = it.arrangoer ?: "",
                )
            },
        )
    }
}
