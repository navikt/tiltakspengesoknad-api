package no.nav.tiltakspenger.soknad.api.tiltak

import io.ktor.server.config.ApplicationConfig
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class TiltakServiceTest {

    private val tiltakspengerArenaClient = mockk<TiltakspengerArenaClient>()

    private val tiltakService = TiltakService(
        applicationConfig = ApplicationConfig("application.test.conf"),
        tiltakspengerArenaClient = tiltakspengerArenaClient,
    )

    @Test
    fun `tiltaksarrangør maskeres når maskerArrangørnavn=true`() {
        runBlocking {
            coEvery { tiltakspengerArenaClient.fetchTiltak(any()) } returns Result.success(
                mockArenaTiltaksaktivitetResponsDTO(),
            )

            val tiltak = tiltakService.hentTiltak("subjectToken", true)
            assertEquals(tiltak.tiltak.first().arrangør, "")
        }
    }

    @Test
    fun `man får all informasjon om tiltaket når maskerArrangørnavn=false`() {
        val arrangørNavn = "Arrangør AS"
        runBlocking {
            coEvery { tiltakspengerArenaClient.fetchTiltak(any()) } returns Result.success(
                mockArenaTiltaksaktivitetResponsDTO(arrangørNavn),
            )

            val tiltak = tiltakService.hentTiltak("subjectToken", false)
            assertEquals(tiltak.tiltak.first().arrangør, arrangørNavn)
        }
    }

    @Test
    fun `ved feil mot tiltakspenger-arena kastes en IllegalStateException`() {
        runBlocking {
            assertThrows<IllegalStateException> {
                coEvery { tiltakspengerArenaClient.fetchTiltak(any()) } returns Result.failure(IllegalStateException())
                tiltakService.hentTiltak("subjectToken", false)
            }.also {
                assertEquals(it.message, "Noe gikk galt under kall til tiltakspenger-arena")
            }
        }
    }

    private fun mockArenaTiltaksaktivitetResponsDTO(arrangør: String = "Arrangør AS") =
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
        )
}
