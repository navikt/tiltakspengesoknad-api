package no.nav.tiltakspenger.soknad.api.util

import io.ktor.http.ContentType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

// WIP

class PdfTools {
    companion object {
        fun konverterPdfTilBilder(pdfByteArray: ByteArray): List<Bilde>{
            val pdfDokument = PDDocument.load(pdfByteArray)
            val renderer = PDFRenderer(pdfDokument)
            val siderSomBilder = (0 until pdfDokument.numberOfPages).map {
                val bilde = renderer.renderImage(it)
                val baos = ByteArrayOutputStream()
                ImageIO.write(bilde, "png", baos)
                Bilde(ContentType.Image.PNG, baos.toByteArray())
            }
            pdfDokument.close()
            return siderSomBilder
        }

        fun slåSammenPdfer(pdfbaListe: List<ByteArray>): ByteArray {
            val pdfMerger = PDFMergerUtility()
            val baosUt = ByteArrayOutputStream()
            pdfMerger.destinationStream = baosUt;
            pdfbaListe.forEach {
                pdfMerger.addSource(ByteArrayInputStream(it))
            }
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly()); //TODO: Sjekk ut memory settings
            return baosUt.toByteArray()
        }
    }
}
class Bilde(
    val type: ContentType,
    val data: ByteArray,
)
