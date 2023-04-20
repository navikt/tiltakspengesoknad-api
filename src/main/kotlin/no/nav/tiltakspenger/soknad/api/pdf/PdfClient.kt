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
import no.nav.tiltakspenger.soknad.api.domain.AnnenUtbetaling
import no.nav.tiltakspenger.soknad.api.domain.Barn
import no.nav.tiltakspenger.soknad.api.domain.Periode
import no.nav.tiltakspenger.soknad.api.domain.Søknad
import no.nav.tiltakspenger.soknad.api.domain.Tiltak
import no.nav.tiltakspenger.soknad.api.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.UUID
import no.nav.tiltakspenger.soknad.api.soknad.BadExtensionException
import no.nav.tiltakspenger.soknad.api.soknad.LOG
import no.nav.tiltakspenger.soknad.api.util.Bilde
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg

internal const val pdfgenPath = "api/v1/genpdf/tpts"
internal const val pdfgenImagePath = "api/v1/genpdf/image/tpts"
internal const val SOKNAD_TEMPLATE = "soknad"

class PdfClient(
    config: ApplicationConfig,
    private val client: HttpClient,
) : PdfGenerator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val pdfEndpoint = config.property("endpoints.pdf").getString()

    override suspend fun genererPdf(søknad: Søknad): ByteArray {
        val søknadDto = mapToPdfDto(søknad)
        try {
            return client.post("$pdfEndpoint/$pdfgenPath/$SOKNAD_TEMPLATE") {
                accept(ContentType.Application.Json)
                header("X-Correlation-ID", UUID.randomUUID())
                contentType(ContentType.Application.Json)
                setBody(objectMapper.writeValueAsString(søknadDto))
            }.body()
        } catch (throwable: Throwable) {
            log.error("Kallet til pdfgen feilet $throwable")
            throw RuntimeException("Kallet til pdfgen feilet $throwable")
        }
    }

    override suspend fun konverterVedlegg(vedlegg: List<Vedlegg>): List<Vedlegg> {
        return vedlegg.map {
            LOG.info("Konverterer vedlegg: ${it.filnavn}")
            val extension = it.filnavn.split(".").last().lowercase()
            when(extension) {
                "pdf" -> {
                    it
                }
                "png" -> {
                    val bilde = genererPdfFraBilde(Bilde(ContentType.Image.PNG, it.dokument))
                    Vedlegg(it.filnavn, bilde)
                }
                "jpg", "jpeg" -> {
                    val bilde = genererPdfFraBilde(Bilde(ContentType.Image.JPEG, it.dokument))
                    Vedlegg(it.filnavn, bilde)
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

    private fun mapToPdfDto(søknad: Søknad): SøknadPdfDto {
        return SøknadPdfDto(
            deltarIKvp = søknad.deltarIKvp,
            periodeMedKvp = mapPeriode(søknad.periodeMedKvp),
            deltarIIntroprogrammet = søknad.deltarIIntroprogrammet,
            periodeMedIntroprogrammet = mapPeriode(søknad.periodeMedIntroprogrammet),
            borPåInstitusjon = søknad.borPåInstitusjon,
            institusjonstype = søknad.institusjonstype,
            tiltak = mapTiltak(søknad.tiltak),
            mottarEllerSøktPensjonsordning = søknad.mottarEllerSøktPensjonsordning,
            pensjon = mapAnnenUtbetaling(søknad.pensjon),
            mottarEllerSøktEtterlønn = søknad.mottarEllerSøktEtterlønn,
            etterlønn = mapAnnenUtbetaling(søknad.etterlønn),
            søkerOmBarnetillegg = søknad.søkerOmBarnetillegg,
            barnSøktBarnetilleggFor = mapBarn(søknad.barnSøktBarnetilleggFor),
        )
    }

    private fun mapPeriode(periode: Periode?) = if (periode == null) {
        null
    } else {
        PeriodePdfDto(
            fra = periode.fra,
            til = periode.til,
        )
    }

    private fun mapTiltak(tiltak: Tiltak) = TiltakPdfDto(
        type = tiltak.type,
        periode = mapPeriode(tiltak.periode)!!,
        antallDagerIUken = tiltak.antallDagerIUken,
    )

    private fun mapAnnenUtbetaling(annenUtbetaling: AnnenUtbetaling?) =
        if (annenUtbetaling == null) {
            null
        } else {
            AnnenUtbetalingPdfDto(
                utbetaler = annenUtbetaling.utbetaler,
                periode = mapPeriode(annenUtbetaling.periode)!!,
            )
        }

    private fun mapBarn(barn: List<Barn>?) = barn?.map {
        BarnPdfDto(
            fornavn = it.fornavn,
            etternavn = it.etternavn,
            fdato = it.fdato,
            bostedsland = it.bostedsland,
        )
    }

    data class PeriodePdfDto(
        val fra: LocalDate,
        val til: LocalDate,
    )

    data class TiltakPdfDto(
        val type: String,
        val periode: PeriodePdfDto,
        val antallDagerIUken: Int,
    )

    data class AnnenUtbetalingPdfDto(
        val utbetaler: String,
        val periode: PeriodePdfDto,
    )

    data class BarnPdfDto(
        val fornavn: String,
        val etternavn: String,
        val fdato: LocalDate,
        val bostedsland: String,
    )

    data class SøknadPdfDto(
        val deltarIKvp: Boolean,
        val periodeMedKvp: PeriodePdfDto?,
        val deltarIIntroprogrammet: Boolean,
        val periodeMedIntroprogrammet: PeriodePdfDto?,
        val borPåInstitusjon: Boolean,
        val institusjonstype: String?,
        val tiltak: TiltakPdfDto,
        val mottarEllerSøktPensjonsordning: Boolean,
        val pensjon: AnnenUtbetalingPdfDto?,
        val mottarEllerSøktEtterlønn: Boolean,
        val etterlønn: AnnenUtbetalingPdfDto?,
        val søkerOmBarnetillegg: Boolean,
        val barnSøktBarnetilleggFor: List<BarnPdfDto>?,
    )
}
