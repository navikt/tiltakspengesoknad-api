package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface SøknadService {
    suspend fun opprettDokumenterOgArkiverIJoark(søknad: Søknad, fnr: String, vedlegg: List<Vedlegg>): String
}
