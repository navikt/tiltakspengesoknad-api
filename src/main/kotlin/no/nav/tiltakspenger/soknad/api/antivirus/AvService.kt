package no.nav.tiltakspenger.soknad.api.antivirus

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface AvService {
    suspend fun scan(vedleggsListe: List<Vedlegg>): List<AvSjekkResultat>
}
