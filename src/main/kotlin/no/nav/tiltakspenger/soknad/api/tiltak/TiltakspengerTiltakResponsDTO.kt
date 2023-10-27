package no.nav.tiltakspenger.soknad.api.tiltak

import java.time.LocalDate
import java.time.LocalDateTime
data class TiltakDeltakelseResponse(
    val id: String,
    val gjennomforing: GjennomforingResponseDTO,
    val startDato: LocalDate?,
    val sluttDato: LocalDate?,
    val status: DeltakerStatusResponseDTO,
    val dagerPerUke: Float?,
    val prosentStilling: Float?,
    val registrertDato: LocalDateTime,
)

data class GjennomforingResponseDTO(
    val id: String,
    val arrangornavn: String,
    val typeNavn: String,
    val arenaKode: String,
//    val status: TiltaksgjennomforingsstatusResponse,
    val startDato: LocalDate?,
    val sluttDato: LocalDate?,
)

enum class DeltakerStatusResponseDTO {
    VENTER_PA_OPPSTART,
    DELTAR,
    HAR_SLUTTET,
    IKKE_AKTUELL,
    FEILREGISTRERT,
    PABEGYNT_REGISTRERING,
    SOKT_INN,
    VENTELISTE,
    VURDERES,
    AVBRUTT,
    FULLFORT,
}
