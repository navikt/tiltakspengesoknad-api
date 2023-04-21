package no.nav.tiltakspenger.soknad.api.pdf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

internal const val pdfgenPath = "api/v1/genpdf/tpts"
internal const val SOKNAD_TEMPLATE = "soknad"

class PdfClient(
    config: ApplicationConfig,
    private val client: HttpClient,
) : PdfGenerator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val pdfEndpoint = config.property("endpoints.pdf").getString()

    override suspend fun genererPdf(søknad: Søknad): ByteArray {
        try {
            return client.post("$pdfEndpoint/$pdfgenPath/$SOKNAD_TEMPLATE") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(ContentType.Application.Json)
                setBody(objectMapper.writeValueAsString(søknad))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Kallet til pdfgen feilet $throwable")
            throw RuntimeException("Kallet til pdfgen feilet $throwable")
        }
    }
}
