package no.nav.tiltakspenger.soknad.api.repository

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.db.DataSource
import no.nav.tiltakspenger.soknad.api.db.PostgresTestcontainer
import no.nav.tiltakspenger.soknad.api.soknad.Applikasjonseier
import no.nav.tiltakspenger.soknad.api.soknad.MottattSøknad
import no.nav.tiltakspenger.soknad.api.soknad.SpørsmålsbesvarelserDTO
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepoImpl
import no.nav.tiltakspenger.soknad.api.soknad.validering.spørsmålsbesvarelser
import no.nav.tiltakspenger.soknad.api.soknad.validering.søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
internal class MottattSøknadRepoTest {
    private val søknadRepo = SøknadRepoImpl()

    init {
        PostgresTestcontainer.start()
    }

    @BeforeEach
    fun setup() {
        Flyway.configure()
            .dataSource(DataSource.hikariDataSource)
            .loggers("slf4j")
            .encoding("UTF-8")
            .cleanDisabled(false)
            .load()
            .run {
                clean()
                migrate()
            }
    }

    @Test
    fun `søknad til tiltakspenger`() {
        val nå = LocalDateTime.now()
        val søknad = søknad()
        val mottattSøknad = genererMottattSøknadForTest(
            opprettet = nå,
            eier = Applikasjonseier.Tiltakspenger,
        )
        søknadRepo.lagre(mottattSøknad)
        val søknaderSomIkkeErJounalført = søknadRepo.hentAlleSøknadDbDtoSomIkkeErJournalført()
        søknaderSomIkkeErJounalført.size shouldBe 1

        // Journalfører søknaden
        val journalførtSøknad = søknaderSomIkkeErJounalført.first().copy(
            søknad = søknad,
            fornavn = "fornavn",
            etternavn = "etternavn",
            journalført = nå,
            journalpostId = "123",
        )
        søknadRepo.oppdater(journalførtSøknad)

        // sender søknaden til saksbehandling-api
        val søknaderSomIkkeErSendtTilSaksbehandlingApi = søknadRepo.hentSøknaderSomSkalSendesTilSaksbehandlingApi()
        søknaderSomIkkeErSendtTilSaksbehandlingApi.size shouldBe 1
        val søknadSendtTilSaksbehandlingApi = søknaderSomIkkeErSendtTilSaksbehandlingApi.first().copy(
            sendtTilVedtak = nå,
        )
        søknadRepo.oppdater(søknadSendtTilSaksbehandlingApi)
        søknadRepo.hentSøknaderSomSkalSendesTilSaksbehandlingApi().size shouldBe 0
    }

    @Test
    fun `søknad til arena`() {
        val nå = LocalDateTime.now()
        val søknad = søknad()
        val mottattSøknad = genererMottattSøknadForTest(
            opprettet = nå,
            eier = Applikasjonseier.Arena,
        )
        søknadRepo.lagre(mottattSøknad)
        val søknaderSomIkkeErJounalført = søknadRepo.hentAlleSøknadDbDtoSomIkkeErJournalført()
        søknaderSomIkkeErJounalført.size shouldBe 1

        // Journalfører søknaden
        val journalførtSøknad = søknaderSomIkkeErJounalført.first().copy(
            søknad = søknad,
            fornavn = "fornavn",
            etternavn = "etternavn",
            journalført = nå,
            journalpostId = "123",
        )
        søknadRepo.oppdater(journalførtSøknad)

        // sender søknaden til saksbehandling-api
        val søknaderSomIkkeErSendtTilSaksbehandlingApi = søknadRepo.hentSøknaderSomSkalSendesTilSaksbehandlingApi()
        søknaderSomIkkeErSendtTilSaksbehandlingApi.size shouldBe 0
    }

    private fun genererMottattSøknadForTest(
        id: SøknadId = SøknadId.random(),
        søknadSpm: SpørsmålsbesvarelserDTO = spørsmålsbesvarelser(),
        fnr: String = "12345678901",
        opprettet: LocalDateTime = LocalDateTime.now(),
        vedlegg: List<Vedlegg> = listOf(
            Vedlegg(
                filnavn = "filnavn",
                contentType = "pdf",
                dokument = ByteArray(1),
                brevkode = "123",
            ),
        ),
        versjon: String = "1",
        acr: String = "acr",
        eier: Applikasjonseier,
    ) = MottattSøknad(
        id = id,
        versjon = versjon,
        søknad = null,
        søknadSpm = søknadSpm,
        vedlegg = vedlegg,
        fnr = fnr,
        acr = acr,
        fornavn = null,
        etternavn = null,
        sendtTilVedtak = null,
        journalført = null,
        journalpostId = null,
        opprettet = opprettet,
        eier = eier,
    )
}
