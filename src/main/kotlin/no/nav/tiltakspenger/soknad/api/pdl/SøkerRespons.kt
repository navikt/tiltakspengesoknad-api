package no.nav.tiltakspenger.soknad.api.pdl

data class SøkerFraPDL(
    val navn: List<Navn>,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
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
        val adressebeskyttelseGradering = avklarGradering(person.adressebeskyttelse)
        return Person(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn,
            forelderBarnRelasjon = person.forelderBarnRelasjon,
            adressebeskyttelseGradering = adressebeskyttelseGradering,
        )
    }
}
