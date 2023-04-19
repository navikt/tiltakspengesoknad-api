package no.nav.tiltakspenger.soknad.api.pdl

data class SøkersBarnFraPDL(
    val navn: List<Navn>,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val fødsel: List<Fødsel>,
    val dødsfall: List<Dødsfall>,
)

data class SøkersBarnFraPDLRespons(
    val hentPerson: SøkersBarnFraPDL?,
)

data class SøkersBarnRespons(
    val data: SøkersBarnFraPDLRespons? = null,
    val errors: List<PdlError> = emptyList(),
) {
    private fun extractPerson(): SøkersBarnFraPDL? {
        if (this.errors.isNotEmpty()) {
            throw IllegalStateException(this.errors.firstOrNull()?.message)
        }
        return this.data?.hentPerson
    }

    fun toPerson(): Person {
        val person = extractPerson() ?: throw IllegalStateException("Fant ikke personen")
        val navn = avklarNavn(person.navn)
        val fødsel = avklarFødsel(person.fødsel)
        val dødsfall = person.dødsfall.isNotEmpty()
        val adressebeskyttelseGradering = avklarGradering(person.adressebeskyttelse)
        return Person(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn,
            adressebeskyttelseGradering = adressebeskyttelseGradering,
            fødselsdato = fødsel.foedselsdato,
            erDød = dødsfall,
        )
    }
}
