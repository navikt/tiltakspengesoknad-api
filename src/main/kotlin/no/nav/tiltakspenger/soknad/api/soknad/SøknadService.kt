package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface SøknadService {
    suspend fun opprettDokumenterOgArkiverIJoark(søknad: SøknadRequest, fnr: String, person: PersonDTO, vedlegg: List<Vedlegg>, acr: String): String
}
