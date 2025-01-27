package no.nav.tiltakspenger.soknad.api.soknad.jobb.journalforendeEnhet

import no.nav.tiltakspenger.soknad.api.pdl.AdressebeskyttelseGradering
import no.nav.tiltakspenger.soknad.api.soknad.jobb.journalforendeEnhet.arbeidsfordeling.ArbeidsfordelingClient
import no.nav.tiltakspenger.soknad.api.soknad.jobb.journalforendeEnhet.arbeidsfordeling.ArbeidsfordelingRequest
import no.nav.tiltakspenger.soknad.api.soknad.jobb.person.Person
import no.nav.tiltakspenger.soknad.api.soknad.log

class JournalforendeEnhetService(
    private val arbeidsfordelingClient: ArbeidsfordelingClient,
) {
    suspend fun finnJournalforendeEnhet(person: Person): String {
        val arbeidsfordelingRequest = ArbeidsfordelingRequest(
            diskresjonskode = getDiskresjonskode(person.adressebeskyttelseGradering),
            geografiskOmraade = person.geografiskTilknytning?.getGT(),
        )
        return arbeidsfordelingClient.hentArbeidsfordeling(arbeidsfordelingRequest)
            .also { log.info { "Fant journalførende enhet $it" } }
    }

    private fun getDiskresjonskode(adressebeskyttelseGradering: AdressebeskyttelseGradering) =
        when (adressebeskyttelseGradering) {
            AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND,
            AdressebeskyttelseGradering.STRENGT_FORTROLIG,
            -> "SPSF"
            AdressebeskyttelseGradering.FORTROLIG -> "SPFO"
            else -> null
        }
}
