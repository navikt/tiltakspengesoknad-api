package no.nav.tiltakspenger.soknad.api.antivirus

import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface AvService {
    suspend fun gjørVirussjekkAvVedlegg(vedleggsListe: List<Vedlegg>)
}
