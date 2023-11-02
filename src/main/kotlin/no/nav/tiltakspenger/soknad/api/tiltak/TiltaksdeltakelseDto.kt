package no.nav.tiltakspenger.soknad.api.tiltak

import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO.TiltakDTO
import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO.TiltakType
import java.time.LocalDate

data class Deltakelsesperiode(
    val fra: LocalDate?,
    val til: LocalDate?,
)

data class TiltaksdeltakelseDto(
    val aktivitetId: String,
    val type: TiltakType,
    val typeNavn: String,
    val deltakelsesPeriode: Deltakelsesperiode,
    val arrangør: String,
) {
    fun erInnenforRelevantTidsrom(): Boolean {
        val datoFor6MånederSiden = LocalDate.now().minusMonths(6)
        val dato2MånederFrem = LocalDate.now().plusMonths(2)

        return if (deltakelsesPeriode.fra == null) {
            true
        } else if (deltakelsesPeriode.til == null) {
            deltakelsesPeriode.fra.isBefore(dato2MånederFrem) && deltakelsesPeriode.fra.isAfter(datoFor6MånederSiden)
        } else {
            deltakelsesPeriode.fra.isBefore(dato2MånederFrem) && deltakelsesPeriode.til.isAfter(datoFor6MånederSiden)
        }
    }
}

fun List<TiltakDTO>.toTiltakDto(maskerArrangørnavn: Boolean): List<TiltaksdeltakelseDto> {
    return this.map {
        TiltaksdeltakelseDto(
            aktivitetId = it.id,
            type = it.gjennomforing.arenaKode,
            typeNavn = it.gjennomforing.typeNavn,
            deltakelsesPeriode = Deltakelsesperiode(
                fra = it.startDato,
                til = it.sluttDato,
            ),
            arrangør = if (maskerArrangørnavn) "" else it.gjennomforing.arrangornavn,
            // status = ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.valueOf(it.status.name),
        )
    }
}
