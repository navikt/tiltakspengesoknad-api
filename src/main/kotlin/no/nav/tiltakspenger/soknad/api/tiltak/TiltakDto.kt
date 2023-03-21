package no.nav.tiltakspenger.soknad.api.tiltak

import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO

data class TiltaksdeltakelseDto(
    val type: ArenaTiltaksaktivitetResponsDTO.TiltakType,
)

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
                )
            },
        )
    }
}
