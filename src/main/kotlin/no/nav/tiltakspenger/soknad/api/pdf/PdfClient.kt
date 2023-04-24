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
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.objectMapper
import no.nav.tiltakspenger.soknad.api.soknad.BadExtensionException
import no.nav.tiltakspenger.soknad.api.soknad.LOG
import no.nav.tiltakspenger.soknad.api.util.Bilde
import no.nav.tiltakspenger.soknad.api.util.PdfTools
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

internal const val pdfgenPath = "api/v1/genpdf/tpts"
internal const val pdfgenImagePath = "api/v1/genpdf/image/tpts"
internal const val SOKNAD_TEMPLATE = "soknad"

class PdfClient(
    config: ApplicationConfig,
    private val client: HttpClient,
) : PdfGenerator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val pdfEndpoint = config.property("endpoints.pdf").getString()

    override suspend fun genererPdf(søknadDTO: SøknadDTO): ByteArray {
        try {
            return client.post("$pdfEndpoint/$pdfgenPath/$SOKNAD_TEMPLATE") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(ContentType.Application.Json)
                setBody(objectMapper.writeValueAsString(søknadDTO))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Kallet til pdfgen feilet $throwable")
            throw RuntimeException("Kallet til pdfgen feilet $throwable")
        }
    }

    override suspend fun konverterVedlegg(vedlegg: List<Vedlegg>): List<Vedlegg> {
        return vedlegg.map { it ->
            LOG.info("Konverterer vedlegg: ${it.filnavn}")
            val baseFileName = it.filnavn.split(".").first().lowercase()
            val extension = it.filnavn.split(".").last().lowercase()
            when (extension) {
                "pdf" -> {
                    val bilder = PdfTools.konverterPdfTilBilder(it.dokument)
                    val enkeltsider = bilder.map { bilde ->
                        genererPdfFraBilde(Bilde(ContentType.Image.PNG, bilde.data))
                    }
                    val resultatPdf = PdfTools.slåSammenPdfer(enkeltsider)
                    Vedlegg("$baseFileName.pdf", resultatPdf)
                }
                "png" -> {
                    val pdfFraBilde = genererPdfFraBilde(Bilde(ContentType.Image.PNG, it.dokument))
                    Vedlegg("$baseFileName.pdf", pdfFraBilde)
                }
                "jpg", "jpeg" -> {
                    val pdfFraBilde = genererPdfFraBilde(Bilde(ContentType.Image.JPEG, it.dokument))
                    Vedlegg("$baseFileName.pdf", pdfFraBilde)
                }
                else -> {
                    throw BadExtensionException("Ugyldig filformat")
                }
            }
        }
    }
    private suspend fun genererPdfFraBilde(bilde: Bilde): ByteArray {
        try {
            return client.post("$pdfEndpoint/$pdfgenImagePath") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(bilde.type)
                setBody(ByteArrayContent(bilde.data))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Kallet til pdfgen feilet $throwable")
            throw RuntimeException("Kallet til pdfgen feilet $throwable")
        }
    }
}
