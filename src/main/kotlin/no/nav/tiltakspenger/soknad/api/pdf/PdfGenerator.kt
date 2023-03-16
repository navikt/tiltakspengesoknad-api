package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.Søknad

interface PdfGenerator {
    suspend fun genererPdf(søknad: Søknad): ByteArray
}
