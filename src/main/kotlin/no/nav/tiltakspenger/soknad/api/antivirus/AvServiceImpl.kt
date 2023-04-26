package no.nav.tiltakspenger.soknad.api.antivirus

import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class AvServiceImpl(
    private val av: AntiVirus,
) : AvService {

    private val log = KotlinLogging.logger { }
    private suspend fun scanVedlegg(vedleggsListe: List<Vedlegg>): List<AvSjekkResultat> {
        val resultat = av.scan(vedleggsListe)
        log.info { resultat }
        return resultat
    }

    override suspend fun gjørVirussjekkAvVedlegg(vedleggsListe: List<Vedlegg>) {
        val resultat = scanVedlegg(vedleggsListe)
        val virusErFunnet = resultat.any { it.resultat == Status.FOUND }
        if (virusErFunnet) {
            throw MalwareFoundException("Skadevare funnet i vedlegg")
        }
    }
}

class MalwareFoundException(message: String) : RuntimeException(message)
