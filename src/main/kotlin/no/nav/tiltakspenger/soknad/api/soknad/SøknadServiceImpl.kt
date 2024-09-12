package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import mu.KotlinLogging
import no.nav.tiltakspenger.libs.common.SøknadId
import no.nav.tiltakspenger.soknad.api.deserialize
import no.nav.tiltakspenger.soknad.api.domain.SøknadDTO
import no.nav.tiltakspenger.soknad.api.joark.JoarkService
import no.nav.tiltakspenger.soknad.api.pdf.PdfService
import no.nav.tiltakspenger.soknad.api.util.sjekkContentType
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

val log = KotlinLogging.logger { }

class SøknadServiceImpl(
    private val pdfService: PdfService,
    private val joarkService: JoarkService,
) : SøknadService {
    override suspend fun opprettDokumenterOgArkiverIJoark(
        spørsmålsbesvarelser: SpørsmålsbesvarelserDTO,
        fnr: String,
        fornavn: String,
        etternavn: String,
        vedlegg: List<Vedlegg>,
        acr: String,
        innsendingTidspunkt: LocalDateTime,
        søknadId: SøknadId,
        callId: String,
    ): Pair<String, SøknadDTO> {
        val vedleggsnavn = vedlegg.map { it.filnavn }
        val søknadDTO = SøknadDTO.toDTO(
            id = søknadId.toString(),
            spørsmålsbesvarelser = spørsmålsbesvarelser,
            fnr = fnr,
            fornavn = fornavn,
            etternavn = etternavn,
            acr = acr,
            innsendingTidspunkt = innsendingTidspunkt,
            vedleggsnavn = vedleggsnavn,
        )
        val pdf = pdfService.lagPdf(søknadDTO)
        log.info { "Generering av søknadsPDF OK" }
        val vedleggSomPdfer = pdfService.konverterVedlegg(vedlegg)
        log.info { "Vedleggskonvertering OK" }
        val journalpostId = joarkService.sendPdfTilJoark(pdf = pdf, søknadDTO = søknadDTO, fnr = fnr, vedlegg = vedleggSomPdfer, søknadId = søknadId, callId = callId)
        return Pair(journalpostId, søknadDTO)
    }

    override suspend fun taInnSøknadSomMultipart(søknadSomMultipart: MultiPartData): Pair<SpørsmålsbesvarelserDTO, List<Vedlegg>> {
        lateinit var spørsmålsbesvarelserDTO: SpørsmålsbesvarelserDTO
        val vedleggListe = mutableListOf<Vedlegg>()
        søknadSomMultipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    spørsmålsbesvarelserDTO = part.toSpørsmålsbesvarelser()
                }

                is PartData.FileItem -> {
                    vedleggListe.add(part.toVedlegg())
                }

                else -> {}
            }
            part.dispose()
        }

        return Pair(spørsmålsbesvarelserDTO, vedleggListe)
    }
}

fun PartData.FileItem.toVedlegg(): Vedlegg {
    val filnavn = this.originalFileName ?: "untitled-${this.hashCode()}"
    val fileBytes = this.streamProvider().readBytes()
    return Vedlegg(filnavn = filnavn, contentType = sjekkContentType(fileBytes), dokument = fileBytes)
}

fun PartData.FormItem.toSpørsmålsbesvarelser(): SpørsmålsbesvarelserDTO {
    if (this.name == "søknad") {
        return deserialize<SpørsmålsbesvarelserDTO>(this.value).validerRequest()
    }
    throw UnrecognizedFormItemException(message = "Recieved multipart form with unknown key ${this.name}")
}

class UnrecognizedFormItemException(message: String) : RuntimeException(message)
class MissingContentException(message: String) : RuntimeException(message)
