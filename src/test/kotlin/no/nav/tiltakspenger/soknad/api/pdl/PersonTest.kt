package no.nav.tiltakspenger.soknad.api.pdl

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class PersonTest {
    private val testPersonUgradert = Person(
        fornavn = "foo",
        mellomnavn = "baz",
        etternavn = "bar",
        adressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT,
        fødselsdato = LocalDate.MAX,
    )

    private val testPersonFortrolig = Person(
        fornavn = "foo",
        mellomnavn = "baz",
        etternavn = "bar",
        adressebeskyttelseGradering = AdressebeskyttelseGradering.FORTROLIG,
        fødselsdato = LocalDate.MAX,
    )

    private val testPersonStrengtFortrolig = Person(
        fornavn = "foo",
        mellomnavn = "baz",
        etternavn = "bar",
        adressebeskyttelseGradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG,
        fødselsdato = LocalDate.MAX,
    )

    private val testPersonStrengtFortroligUtland = Person(
        fornavn = "foo",
        mellomnavn = "baz",
        etternavn = "bar",
        adressebeskyttelseGradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND,
        fødselsdato = LocalDate.MAX,
    )

    @Test
    fun `toPersonDTO skal returnere personens navn og en tom liste med barn når det ikke er noen barn`() {
        val personDTO = testPersonUgradert.toPersonDTO(emptyList())
        assertEquals(testPersonUgradert.fornavn, personDTO.fornavn)
        assertEquals(testPersonUgradert.mellomnavn, personDTO.mellomnavn)
        assertEquals(testPersonUgradert.etternavn, personDTO.etternavn)
        assertTrue(personDTO.barn.isEmpty())
    }

    @Test
    fun `toPersonDTO skal returnere personens navn og en liste med barn når man oppgir barn`() {
        val personDTO = testPersonUgradert.toPersonDTO(listOf(testPersonUgradert))
        assertEquals(testPersonUgradert.fornavn, personDTO.fornavn)
        assertEquals(testPersonUgradert.mellomnavn, personDTO.mellomnavn)
        assertEquals(testPersonUgradert.etternavn, personDTO.etternavn)
        assertTrue(personDTO.barn.size == 1)

        val ugradertBart = personDTO.barn.get(0)
        assertEquals(testPersonUgradert.fornavn, ugradertBart.fornavn)
        assertEquals(testPersonUgradert.mellomnavn, ugradertBart.mellomnavn)
        assertEquals(testPersonUgradert.etternavn, ugradertBart.etternavn)
    }

    @Test
    fun `toPersonDTO skal ikke returnere navn på barn som har AdressebeskyttelseGradering FORTROLIG`() {
        val personDTO = testPersonUgradert.toPersonDTO(listOf(testPersonFortrolig))
        val fortroligBarn = personDTO.barn.get(0)
        assertNull(fortroligBarn.fornavn)
        assertNull(fortroligBarn.mellomnavn)
        assertNull(fortroligBarn.etternavn)
    }

    @Test
    fun `toPersonDTO skal ikke returnere navn på barn som har AdressebeskyttelseGradering STRENGT_FORTROLIG`() {
        val personDTO = testPersonUgradert.toPersonDTO(listOf(testPersonStrengtFortrolig))
        val fortroligBarn = personDTO.barn.get(0)
        assertNull(fortroligBarn.fornavn)
        assertNull(fortroligBarn.mellomnavn)
        assertNull(fortroligBarn.etternavn)
    }

    @Test
    fun `toPersonDTO skal ikke returnere navn på barn som har AdressebeskyttelseGradering STRENGT_FORTROLIG_UTLAND`() {
        val personDTO = testPersonUgradert.toPersonDTO(listOf(testPersonStrengtFortroligUtland))
        val fortroligBarn = personDTO.barn.get(0)
        assertNull(fortroligBarn.fornavn)
        assertNull(fortroligBarn.mellomnavn)
        assertNull(fortroligBarn.etternavn)
    }
}
