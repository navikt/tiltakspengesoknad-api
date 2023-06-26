package no.nav.tiltakspenger.soknad.api.antivirus

import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

class AvServiceImpl(
    private val av: AntiVirus,
) : AvService {

    private val log = KotlinLogging.logger { }
    private val securelog = KotlinLogging.logger("tjenestekall")

    override suspend fun gjørVirussjekkAvVedlegg(vedleggsListe: List<Vedlegg>) {
        val resultat = av.scan(vedleggsListe)
        val virusErFunnet = resultat.any { it.resultat == Status.FOUND }

        resultat.forEach {
            if (it.resultat == Status.FOUND) {
                securelog.info { "Fant skadevare i vedlegg ${it.filnavn}" }
            }
            if (it.resultat === Status.ERROR) {
                securelog.info { "Noe gikk galt under virusscan av fil ${it.filnavn}" }
            }
        }

        if (virusErFunnet) {
            throw MalwareFoundException("Skadevare funnet i vedlegg")
        }

        log.info { "Virussjekk ok" }
    }
}

class MalwareFoundException(message: String) : RuntimeException(message)
