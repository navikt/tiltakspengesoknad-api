package no.nav.tiltakspenger.soknad.api.antivirus

import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class AvServiceImpl(
    private val av: AntiVirus,
) : AvService {

    private val log = KotlinLogging.logger { }

    override suspend fun gjørVirussjekkAvVedlegg(vedleggsListe: List<Vedlegg>) {
        val resultat = av.scan(vedleggsListe).also { log.info { it } }
        val virusErFunnet = resultat.any { it.resultat == Status.FOUND }
        if (virusErFunnet) {
            throw MalwareFoundException("Skadevare funnet i vedlegg")
        }
    }
}

class MalwareFoundException(message: String) : RuntimeException(message)
