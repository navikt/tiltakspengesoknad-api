package no.nav.tiltakspenger.soknad.api.pdl

import java.time.LocalDate

data class Person(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val fødselsdato: LocalDate? = null,
    val forelderBarnRelasjon: List<ForelderBarnRelasjon>? = emptyList(),
    val adressebeskyttelseGradering: AdressebeskyttelseGradering,
) {
    fun toPersonDTO(barn: List<Person> = emptyList()): PersonDTO {
        return PersonDTO(
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            barn = barn.map {
                if (it.adressebeskyttelseGradering === AdressebeskyttelseGradering.UGRADERT) {
                    BarnDTO(fødselsdato = it.fødselsdato!!, navn = it.fornavn)
                } else {
                    BarnDTO(fødselsdato = it.fødselsdato!!)
                }
            },
        )
    }
}

data class BarnDTO(
    val fødselsdato: LocalDate,
    val navn: String? = null,
)

data class PersonDTO(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val barn: List<BarnDTO> = emptyList(),
)
