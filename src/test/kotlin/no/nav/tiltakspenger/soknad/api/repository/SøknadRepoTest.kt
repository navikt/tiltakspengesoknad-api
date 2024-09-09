package no.nav.tiltakspenger.soknad.api.repository

import no.nav.tiltakspenger.soknad.api.db.PostgresTestcontainer
import no.nav.tiltakspenger.soknad.api.db.flywayCleanAndMigrate
import no.nav.tiltakspenger.soknad.api.soknad.Søknad
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepoImpl
import no.nav.tiltakspenger.soknad.api.soknad.validering.spørsmålsbesvarelser
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@Testcontainers
internal class SøknadRepoTest {
    private val søknadRepo = SøknadRepoImpl()

    init {
        PostgresTestcontainer.start()
    }

    @BeforeEach
    fun setup() {
        flywayCleanAndMigrate()
    }

    @Test
    fun `lagre søknad`() {
        val uuid = UUID.randomUUID()
        val nå = LocalDateTime.now()
        val fnr = "12345678901"
        val spm = spørsmålsbesvarelser()
        val søknad = Søknad(
            id = uuid,
            versjon = "1",
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
            sendtTilVedtak = nå,
            journalført = nå,
            opprettet = nå,
        )
        søknadRepo.lagre(søknad)
    }
}
