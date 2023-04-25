package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.server.config.ApplicationConfig
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals

internal class PdlServiceTest {
    val testFødselsnummer = "123"
    val testBarnFødselsnummer = "456"

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    fun mockEndringsMetadata(): EndringsMetadata {
        return EndringsMetadata(
            endringer = emptyList(),
            master = "test",
        )
    }

    fun mockFolkeregisterMetadata(): FolkeregisterMetadata {
        return FolkeregisterMetadata(
            aarsak = null,
            ajourholdstidspunkt = null,
            gyldighetstidspunkt = null,
            kilde = null,
            opphoerstidspunkt = null,
            sekvens = null,
        )
    }

    fun mockNavn(): Navn {
        return Navn(
            fornavn = "foo",
            etternavn = "bar",
            metadata = mockEndringsMetadata(),
            folkeregistermetadata = mockFolkeregisterMetadata(),
        )
    }

    fun mockFødsel(): Fødsel {
        return Fødsel(
            foedselsdato = LocalDate.MAX,
            metadata = mockEndringsMetadata(),
            folkeregistermetadata = mockFolkeregisterMetadata(),
        )
    }

    fun mockSøkerRespons(forelderBarnRelasjon: List<ForelderBarnRelasjon> = emptyList()): SøkerRespons {
        return SøkerRespons(
            data = SøkerFraPDLRespons(
                hentPerson = SøkerFraPDL(
                    navn = listOf(mockNavn()),
                    adressebeskyttelse = emptyList(),
                    forelderBarnRelasjon = forelderBarnRelasjon,
                    doedsfall = emptyList(),
                ),
            ),
        )
    }

    fun mockForelderBarnRelasjon(
        rolle: ForelderBarnRelasjonRolle = ForelderBarnRelasjonRolle.BARN,
        ident: String = testBarnFødselsnummer,
    ): ForelderBarnRelasjon {
        return ForelderBarnRelasjon(
            relatertPersonsRolle = rolle,
            relatertPersonsIdent = ident,
            folkeregistermetadata = mockFolkeregisterMetadata(),
            metadata = mockEndringsMetadata(),
        )
    }

    private val søkersBarnMock: SøkersBarnRespons = SøkersBarnRespons(
        data = SøkersBarnFraPDLRespons(
            hentPerson = SøkersBarnFraPDL(
                navn = listOf(mockNavn()),
                adressebeskyttelse = emptyList(),
                foedsel = listOf(mockFødsel()),
                doedsfall = emptyList(),
            ),
        ),
    )

    private val mockedTokenXClient = mockk<PdlClientTokenX>()
    private val mockedCredentialsClient = mockk<PdlCredentialsClient>()

    private val pdlService = PdlService(
        ApplicationConfig("application.test.conf"),
        pdlClientTokenX = mockedTokenXClient,
        pdlClientCredentials = mockedCredentialsClient,
    )

    @Test
    fun `ved kall på hentPersonaliaMedBarn skal man hente data om søker med oppgitt fnr med tokenX, etterfulgt av å hente data om søkers barn med client credentials`() {
        val token = "token"
        runBlocking {
            mockedCredentialsClient.also { mock ->
                coEvery { mock.fetchBarn(any()) } returns Result.success(søkersBarnMock)
            }
            mockedTokenXClient.also { mock ->
                coEvery { mock.fetchSøker(any(), any()) } returns Result.success(
                    mockSøkerRespons(
                        forelderBarnRelasjon = listOf(
                            mockForelderBarnRelasjon(),
                        ),
                    ),
                )
            }
            pdlService.hentPersonaliaMedBarn(
                fødselsnummer = testFødselsnummer,
                subjectToken = token,
            )
            coVerify { mockedTokenXClient.fetchSøker(testFødselsnummer, token) }
            coVerify { mockedCredentialsClient.fetchBarn(testBarnFødselsnummer) }
        }
    }

    @Test
    fun `ved kall på hentPersonaliaMedBarn skal man ikke hente data om barn dersom det ikke fantes noen barn i søkerens forelderBarnRelasjon`() {
        val token = "token"
        runBlocking {
            mockedTokenXClient.also { mock ->
                coEvery { mock.fetchSøker(any(), any()) } returns Result.success(mockSøkerRespons())
            }
            pdlService.hentPersonaliaMedBarn(
                fødselsnummer = testFødselsnummer,
                subjectToken = token,
            )
            coVerify { mockedTokenXClient.fetchSøker(testFødselsnummer, token) }
            coVerify(exactly = 0) { mockedCredentialsClient.fetchBarn(any()) }
        }
    }

    @Test
    fun `når fetchSøker med tokenx mot PDL feiler, kastes en IllegalStateExcepiton`() {
        val token = "token"
        assertThrows<IllegalStateException> {
            mockedTokenXClient.also { mock ->
                coEvery { mock.fetchSøker(any(), any()) } returns Result.failure(Exception())
            }
            runBlocking {
                pdlService.hentPersonaliaMedBarn(
                    fødselsnummer = testFødselsnummer,
                    subjectToken = token,
                )
            }
        }.also {
            assertEquals("Noe gikk galt under kall til PDL", it.message)
        }
    }
}
