package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.Søknad

interface PdfService {
    suspend fun lagPdf(søknad: Søknad): ByteArray
}
