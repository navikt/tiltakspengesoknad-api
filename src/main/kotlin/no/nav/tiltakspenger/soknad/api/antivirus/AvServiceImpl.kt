package no.nav.tiltakspenger.soknad.api.antivirus

import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class AvServiceImpl(
    private val av: AntiVirus,
) : AvService {

    private val log = KotlinLogging.logger { }
    override suspend fun scan(vedleggsListe: List<Vedlegg>): List<AvSjekkResultat> {
        val resultat = av.scan(vedleggsListe)
        log.info { resultat }
        return resultat
    }
}
