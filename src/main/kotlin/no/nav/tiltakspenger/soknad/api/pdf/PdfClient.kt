package no.nav.tiltakspenger.soknad.api.pdf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.objectMapper
import no.nav.tiltakspenger.soknad.api.soknad.routes.LOG
import no.nav.tiltakspenger.soknad.api.util.Bilde
import no.nav.tiltakspenger.soknad.api.util.Detect.APPLICATON_PDF
import no.nav.tiltakspenger.soknad.api.util.Detect.IMAGE_JPEG
import no.nav.tiltakspenger.soknad.api.util.Detect.IMAGE_PNG
import no.nav.tiltakspenger.soknad.api.util.Detect.detect
import no.nav.tiltakspenger.soknad.api.util.PdfTools
import no.nav.tiltakspenger.soknad.api.util.UnsupportedContentException
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

internal const val PDFGEN_PATH = "api/v1/genpdf/tpts"
internal const val PDFGEN_IMAGE_PATH = "api/v1/genpdf/image/tpts"
internal const val SOKNAD_TEMPLATE = "soknad"

class PdfClient(
    config: ApplicationConfig,
    private val client: HttpClient,
) : PdfGenerator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val pdfEndpoint = config.property("endpoints.pdf").getString()

    override suspend fun genererPdf(søknad: Søknad): ByteArray {
        try {
            log.info("Starter generering av søknadspdf")
            return client.post("$pdfEndpoint/$PDFGEN_PATH/$SOKNAD_TEMPLATE") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(ContentType.Application.Json)
                setBody(objectMapper.writeValueAsString(søknad))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Feilet å lage PDF for søknad ${søknad.id}", throwable)
            throw RuntimeException("Feilet å lage PDF for søknad ${søknad.id}", throwable)
        }
    }

    override suspend fun konverterVedlegg(vedlegg: List<Vedlegg>): List<Vedlegg> {
        return vedlegg.map {
            LOG.info("Starter konvertering av vedlegg}")
            val contentType = it.dokument.detect()
            when (contentType) {
                APPLICATON_PDF -> {
                    LOG.info("Oppdaget PDF-vedlegg, konverterer til bilde")
                    val bilder = PdfTools.konverterPdfTilBilder(it.dokument)
                    LOG.info("Konverterer bilder tilbake til PDF")
                    val enkeltsider = bilder.map { bilde ->
                        genererPdfFraBilde(Bilde(ContentType.Image.PNG, bilde.data))
                    }
                    val resultatPdf = PdfTools.slåSammenPdfer(enkeltsider)
                    Vedlegg(it.filnavn, "application/pdf", resultatPdf)
                }

                IMAGE_PNG -> {
                    LOG.info("Oppdaget PNG-vedlegg, konverterer til PDF")
                    val pdfFraBilde = genererPdfFraBilde(Bilde(ContentType.Image.PNG, it.dokument))
                    Vedlegg("$${it.filnavn}-konvertert.pdf", "application/pdf", pdfFraBilde)
                }

                IMAGE_JPEG -> {
                    LOG.info("Oppdaget JPEG-vedlegg, konverterer til PDF")
                    val pdfFraBilde = genererPdfFraBilde(Bilde(ContentType.Image.JPEG, it.dokument))
                    Vedlegg("$${it.filnavn}-konvertert.pdf", "application/pdf", pdfFraBilde)
                }

                else -> {
                    throw UnsupportedContentException("Ugyldig filformat")
                }
            }
        }
    }

    private suspend fun genererPdfFraBilde(bilde: Bilde): ByteArray {
        try {
            return client.post("$pdfEndpoint/$PDFGEN_IMAGE_PATH") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(bilde.type)
                setBody(ByteArrayContent(bilde.data))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Feilet å generere PDF fra bilde med Content-Type ${bilde.type}", throwable)
            throw RuntimeException("Feilet å generere PDF fra bilde med Content-Type ${bilde.type}", throwable)
        }
    }
}
