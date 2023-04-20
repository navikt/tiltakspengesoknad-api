package no.nav.tiltakspenger.soknad.api.pdf

import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

interface PdfGenerator {
    suspend fun genererPdf(søknad: Søknad): ByteArray
    suspend fun konverterVedlegg(vedlegg: List<Vedlegg>): List<Vedlegg>
}
