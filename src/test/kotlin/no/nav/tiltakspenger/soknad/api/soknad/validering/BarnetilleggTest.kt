package no.nav.tiltakspenger.soknad.api.soknad.validering

import io.kotest.matchers.collections.shouldContain
import no.nav.tiltakspenger.soknad.api.mockBarnetillegg
import no.nav.tiltakspenger.soknad.api.mockManueltRegistrertBarn
import no.nav.tiltakspenger.soknad.api.mockRegistrertBarn
import no.nav.tiltakspenger.soknad.api.mockSpørsmålsbesvarelser
import no.nav.tiltakspenger.soknad.api.mockTiltak
import no.nav.tiltakspenger.soknad.api.soknad.Periode
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class BarnetilleggTest {
    @Test
    fun `fødselsdato registrerte barn kan ikke være mer enn 16 år tidligere enn tiltaket`() {
        mockSpørsmålsbesvarelser(
            barnetillegg = mockBarnetillegg(
                registrerteBarnSøktBarnetilleggFor = listOf(
                    mockRegistrertBarn(
                        fødselsdato = LocalDate.of(2008, 12, 31),
                    ),
                ),
            ),
            tiltak = mockTiltak(
                periode = Periode(
                    fra = LocalDate.of(2025, 1, 1),
                    til = LocalDate.of(2025, 4, 1),
                ),
            ),
        ).valider() shouldContain "Kan ikke søke for registrerte barn som er mer enn 16 år når tiltaket starter"
    }

    @Test
    fun `fornavn på manuelt registrerte barn, skal ikke overskride maksgrense på 25 tegn`() {
        mockSpørsmålsbesvarelser(
            barnetillegg = mockBarnetillegg(
                manueltRegistrerteBarnSøktBarnetilleggFor = listOf(
                    mockManueltRegistrertBarn(
                        fornavn = "Test Test Test Test Test Test Test Test",
                        mellomnavn = "Test",
                        etternavn = "Test",
                    ),
                ),
            ),
        ).valider() shouldContain "Manuelt registrert barn er ugyldig: fornavn, mellomnavn eller etternavn overskrider maksgrense på 25 tegn"
    }

    @Test
    fun `mellomnavn på manuelt registrerte barn, skal ikke overskride maksgrense på 25 tegn`() {
        mockSpørsmålsbesvarelser(
            barnetillegg = mockBarnetillegg(
                manueltRegistrerteBarnSøktBarnetilleggFor = listOf(
                    mockManueltRegistrertBarn(
                        fornavn = "Test",
                        mellomnavn = "Test Test Test Test Test Test Test Test",
                        etternavn = "Test",
                    ),
                ),
            ),
        ).valider() shouldContain "Manuelt registrert barn er ugyldig: fornavn, mellomnavn eller etternavn overskrider maksgrense på 25 tegn"
    }

    @Test
    fun `etternavn på manuelt registrerte barn, skal ikke overskride maksgrense på 25 tegn`() {
        mockSpørsmålsbesvarelser(
            barnetillegg = mockBarnetillegg(
                manueltRegistrerteBarnSøktBarnetilleggFor = listOf(
                    mockManueltRegistrertBarn(
                        fornavn = "Test",
                        mellomnavn = "Test",
                        etternavn = "Test Test Test Test Test Test Test Test",
                    ),
                ),
            ),
        ).valider() shouldContain "Manuelt registrert barn er ugyldig: fornavn, mellomnavn eller etternavn overskrider maksgrense på 25 tegn"
    }
}
