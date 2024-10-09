package no.nav.tiltakspenger.soknad.api.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SøkerFraPDL(
    val navn: List<Navn>,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val foedselsdato: List<Fødsel>,

    // KEW Denne er midlertidig fjernet siden den ikke brukes per nå: https://trello.com/c/NQzRwuNf/1129-fjern-foreldre-barn-relasjon-i-personquery-mot-pdl
    // val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
    val doedsfall: List<Dødsfall>,
)

data class SøkerFraPDLRespons(
    val hentPerson: SøkerFraPDL?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SøkerRespons(
    val data: SøkerFraPDLRespons? = null,
    val errors: List<PdlError> = emptyList(),
) {
    private fun extractPerson(): SøkerFraPDL? {
        if (this.errors.isNotEmpty()) {
            throw IllegalStateException(this.errors.firstOrNull()?.toString())
        }
        return this.data?.hentPerson
    }

    fun toPerson(): Person {
        val person = extractPerson() ?: throw IllegalStateException("Fant ikke personen")
        val navn = avklarNavn(person.navn)
        val fødsel = avklarFødsel(person.foedselsdato)
        if (person.doedsfall.isNotEmpty()) {
            throw IllegalStateException("Søker er registrert som død i PDL")
        }
        val adressebeskyttelseGradering = avklarGradering(person.adressebeskyttelse)
        return Person(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn,
            fødselsdato = fødsel.foedselsdato,
            adressebeskyttelseGradering = adressebeskyttelseGradering,
            erDød = false,
        )
    }
}
