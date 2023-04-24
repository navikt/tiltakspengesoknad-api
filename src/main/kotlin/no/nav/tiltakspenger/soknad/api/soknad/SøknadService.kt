package no.nav.tiltakspenger.soknad.api.soknad

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface SøknadService {
    suspend fun opprettDokumenterOgArkiverIJoark(søknad: SøknadDTO, fnr: String, vedlegg: List<Vedlegg>): String
}
