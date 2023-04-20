package no.nav.tiltakspenger.soknad.api.pdl

data class SøkerFraPDL(
    val navn: List<Navn>,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
    val dødsfall: List<Dødsfall>,
    )

data class SøkerFraPDLRespons(
    val hentPerson: SøkerFraPDL?,
)

data class SøkerRespons(
    val data: SøkerFraPDLRespons? = null,
    val errors: List<PdlError> = emptyList(),
) {
    private fun extractPerson(): SøkerFraPDL? {
        if (this.errors.isNotEmpty()) {
            throw IllegalStateException(this.errors.firstOrNull()?.message)
        }
        return this.data?.hentPerson
    }

    fun toPerson(): Person {
        val person = extractPerson() ?: throw IllegalStateException("Fant ikke personen")
        val navn = avklarNavn(person.navn)
        if (person.dødsfall.isNotEmpty()){
            throw IllegalStateException("Døde personer kan ikke søke om tiltakspenger")
        }
        val adressebeskyttelseGradering = avklarGradering(person.adressebeskyttelse)
        return Person(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn,
            forelderBarnRelasjon = person.forelderBarnRelasjon,
            adressebeskyttelseGradering = adressebeskyttelseGradering,
            erDød = false,
        )
    }
}
