package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class TiltakServiceTest {

    private val tiltakspengerTiltakClient = mockk<TiltakspengerTiltakClient>()

    private val tiltakService = TiltakService(
        applicationConfig = ApplicationConfig("application.test.conf"),
        tiltakspengerTiltakClient = tiltakspengerTiltakClient,
    )

    @Test
    fun `tiltaksarrangør maskeres når maskerArrangørnavn=true`() {
        runBlocking {
            coEvery { tiltakspengerTiltakClient.fetchTiltak(any()) } returns Result.success(
                mockTiltakspengerTiltakResponsDTO(),
            )

            val tiltak = tiltakService.hentTiltak("subjectToken", true)
            assertEquals(tiltak.tiltak.first().arrangør, "")
        }
    }

    @Test
    fun `man får all informasjon om tiltaket når maskerArrangørnavn=false`() {
        val arrangørNavn = "Arrangør AS"
        runBlocking {
            coEvery { tiltakspengerTiltakClient.fetchTiltak(any()) } returns Result.success(
                mockTiltakspengerTiltakResponsDTO(arrangørNavn),
            )

            val tiltak = tiltakService.hentTiltak("subjectToken", false)
            assertEquals(tiltak.tiltak.first().arrangør, arrangørNavn)
        }
    }

    @Test
    fun `ved feil mot tiltakspenger-arena kastes en IllegalStateException`() {
        runBlocking {
            assertThrows<IllegalStateException> {
                coEvery { tiltakspengerTiltakClient.fetchTiltak(any()) } returns Result.failure(IllegalStateException())
                tiltakService.hentTiltak("subjectToken", false)
            }.also {
                assertEquals(it.message, "Noe gikk galt under kall til tiltakspenger-arena")
            }
        }
    }

/*    private fun mockArenaTiltaksaktivitetResponsDTO(arrangør: String = "Arrangør AS") =
        ArenaTiltaksaktivitetResponsDTO(
            tiltaksaktiviteter = listOf(
                ArenaTiltaksaktivitetResponsDTO.TiltaksaktivitetDTO(
                    tiltakType = ArenaTiltaksaktivitetResponsDTO.TiltakType.ABIST,
                    aktivitetId = "",
                    tiltakLokaltNavn = "",
                    arrangoer = arrangør,
                    bedriftsnummer = "123456789",
                    deltakelsePeriode = null,
                    deltakelseProsent = 100f,
                    deltakerStatusType = ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.DELAVB,
                    statusSistEndret = null,
                    begrunnelseInnsoeking = null,
                    antallDagerPerUke = null,
                ),
            ),
        )*/

    private fun mockTiltakspengerTiltakResponsDTO(arrangør: String = "Arrangør AS") =
        listOf(
            TiltakDeltakelseResponse(
                id = "123456",
                gjennomforing = GjennomforingResponseDTO(
                    id = "123456",
                    arenaKode = "ABIST",
                    typeNavn = "typenavn",
                    arrangornavn = arrangør,
                    startDato = LocalDate.now(),
                    sluttDato = LocalDate.now(),
                ),
                startDato = null,
                sluttDato = null,
                status = DeltakerStatusResponseDTO.DELTAR,
                dagerPerUke = null,
                prosentStilling = null,
                registrertDato = LocalDateTime.now(),
            ),
        )
}
