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
    val deltakelsePeriode: Deltakelsesperiode,
    val arrangør: String,
    val status: ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType,
) {
    fun erInnenforRelevantTidsrom(): Boolean {
        val datoFor6MånederSiden = LocalDate.now().minusMonths(6)
        val dato2MånederFrem = LocalDate.now().plusMonths(2)

        return if (deltakelsePeriode.fra == null) {
            true
        } else if (deltakelsePeriode.til == null) {
            deltakelsePeriode.fra.isBefore(dato2MånederFrem) && deltakelsePeriode.fra.isAfter(datoFor6MånederSiden)
        } else {
            deltakelsePeriode.fra.isBefore(dato2MånederFrem) && deltakelsePeriode.til.isAfter(datoFor6MånederSiden)
        }
    }

    fun harRelevantStatus(): Boolean {
        return status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.AKTUELL ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.JATAKK ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.FULLF ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.DELAVB ||
            status == ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.TILBUD
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
                    typeNavn = it.tiltakType.navn,
                    deltakelsePeriode = Deltakelsesperiode(
                        fra = it.deltakelsePeriode?.fom,
                        til = it.deltakelsePeriode?.tom,
                    ),
                    arrangør = it.arrangoer ?: "",
                    status = it.deltakerStatusType,
                )
            },
        )
    }
}
