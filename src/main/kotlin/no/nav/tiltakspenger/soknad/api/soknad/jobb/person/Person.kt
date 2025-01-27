package no.nav.tiltakspenger.soknad.api.soknad.jobb.person

import io.ktor.util.toUpperCasePreservingASCIIRules
import no.nav.tiltakspenger.soknad.api.pdl.AdressebeskyttelseGradering
import no.nav.tiltakspenger.soknad.api.pdl.Navn

data class Person(
    val navn: Navn,
    val adressebeskyttelseGradering: AdressebeskyttelseGradering,
    val geografiskTilknytning: GeografiskTilknytning?,
)

data class GeografiskTilknytning(
    val gtType: String,
    val gtKommune: String?,
    val gtBydel: String?,
    val gtLand: String?,
) {
    fun getGT(): String? =
        when (gtType.toUpperCasePreservingASCIIRules()) {
            "KOMMUNE" -> gtKommune
            "BYDEL" -> gtBydel
            "UTLAND" -> gtLand
            "UDEFINERT" -> gtType
            else -> null
        }
}
