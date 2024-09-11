package no.nav.tiltakspenger.soknad.api.repository

import no.nav.tiltakspenger.soknad.api.db.PostgresTestcontainer
import no.nav.tiltakspenger.soknad.api.db.flywayCleanAndMigrate
import no.nav.tiltakspenger.soknad.api.soknad.SøknadDbDTO
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepoImpl
import no.nav.tiltakspenger.soknad.api.soknad.validering.spørsmålsbesvarelser
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.UUID

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
        val søknadDbDTO = SøknadDbDTO(
            id = uuid,
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
            fornavn = "fornavn",
            etternavn = "etternavn",
            sendtTilVedtak = nå,
            journalført = nå,
            journalpostId = "123",
            opprettet = nå,
        )
        søknadRepo.lagre(søknadDbDTO)
    }
}
