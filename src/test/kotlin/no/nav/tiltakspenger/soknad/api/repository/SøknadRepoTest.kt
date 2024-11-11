package no.nav.tiltakspenger.soknad.api.repository

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.db.PostgresTestcontainer
import no.nav.tiltakspenger.soknad.api.db.gcpFlyway
import no.nav.tiltakspenger.soknad.api.soknad.Applikasjonseier
import no.nav.tiltakspenger.soknad.api.soknad.Søknad
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepoImpl
import no.nav.tiltakspenger.soknad.api.soknad.validering.spørsmålsbesvarelser
import no.nav.tiltakspenger.soknad.api.soknad.validering.søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
internal class SøknadRepoTest {
    private val søknadRepo = SøknadRepoImpl()

    init {
        PostgresTestcontainer.start()
    }

    @BeforeEach
    fun setup() {
        // Vi ønsker ikke kjøre lokale migreringsskript for testene.
        gcpFlyway().migrate()
    }

    @Test
    fun `lagre søknad`() {
        val id = SøknadId.random()
        val nå = LocalDateTime.now()
        val fnr = "12345678901"
        val spm = spørsmålsbesvarelser()
        val søknad = søknad()
        val søknadDbDTO = Søknad(
            id = id,
            versjon = "1",
            søknad = null,
            søknadSpm = spm,
            vedlegg = listOf(
                Vedlegg(
                    filnavn = "filnavn",
                    contentType = "pdf",
                    dokument = ByteArray(1),
                    brevkode = "123",
                ),
            ),
            fnr = fnr,
            acr = "acr",
            fornavn = null,
            etternavn = null,
            sendtTilVedtak = null,
            journalført = null,
            journalpostId = null,
            opprettet = nå,
            eier = Applikasjonseier.Arena,
        )

        søknadRepo.lagre(søknadDbDTO)
        val søknaderSomIkkeErJounalført = søknadRepo.hentAlleSøknadDbDtoSomIkkeErJournalført()
        søknaderSomIkkeErJounalført.size shouldBe 1

        // ----

        val journalførtSøknad = søknaderSomIkkeErJounalført.first().copy(
            søknad = søknad,
            fornavn = "fornavn",
            etternavn = "etternavn",
            journalført = nå,
            journalpostId = "123",
        )
        søknadRepo.oppdater(journalførtSøknad)
        val søknaderSomIkkeErSendtTilVedtak = søknadRepo.hentAlleSøknadDbDtoSomErJournalførtMenIkkeSendtTilVedtak()
        søknaderSomIkkeErSendtTilVedtak.size shouldBe 1

        // ----

        val søknadSendtTilVedtak = søknaderSomIkkeErSendtTilVedtak.first().copy(
            sendtTilVedtak = nå,
        )
        søknadRepo.oppdater(søknadSendtTilVedtak)
        søknadRepo.hentAlleSøknadDbDtoSomErJournalførtMenIkkeSendtTilVedtak().size shouldBe 0
    }
}
