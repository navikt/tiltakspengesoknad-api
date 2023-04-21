package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO

interface PdfService {
    suspend fun lagPdf(søknadDTO: SøknadDTO): ByteArray
}
