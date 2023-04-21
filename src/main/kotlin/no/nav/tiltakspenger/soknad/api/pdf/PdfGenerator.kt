package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

interface PdfGenerator {
    suspend fun genererPdf(søknadDTO: SøknadDTO): ByteArray
}
