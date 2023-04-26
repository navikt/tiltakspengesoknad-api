package no.nav.tiltakspenger.soknad.api.antivirus

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface AntiVirus {
    suspend fun scan(vedleggsListe: List<Vedlegg>): List<AvSjekkResultat> // TODO: Endre returtype
}
