package no.nav.tiltakspenger.soknad.api.soknad.jobb.person

import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.libs.personklient.pdl.dto.Navn

interface PersonGateway {
    suspend fun hentNavnForFnr(fnr: Fnr): Navn
}
