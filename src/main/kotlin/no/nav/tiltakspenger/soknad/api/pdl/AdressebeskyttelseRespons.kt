package no.nav.tiltakspenger.soknad.api.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class AdressebeskyttelseFraPDL(
    val adressebeskyttelse: List<Adressebeskyttelse>,
)

data class AdressebeskyttelseFraPDLRespons(
    val hentPerson: AdressebeskyttelseFraPDL?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AdressebeskyttelseRespons(
    val data: AdressebeskyttelseFraPDLRespons? = null,
    val errors: List<PdlError> = emptyList(),
) {
    private fun extractPerson(): AdressebeskyttelseFraPDL? {
        if (this.errors.isNotEmpty()) {
            throw IllegalStateException(this.errors.firstOrNull()?.message)
        }
        return this.data?.hentPerson
    }

    fun toAdressebeskyttelseGradering(): AdressebeskyttelseGradering {
        val person = extractPerson() ?: throw IllegalStateException("Fant ikke personen")
        return avklarGradering(person.adressebeskyttelse)
    }
}
