package pdf

import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import data.R
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

class Logbook(contentsByteArray: ByteArray) {
    private val logger: Logger get() = LoggerFactory.getLogger(this::class.java)

    private val contentsPDF = if(contentsByteArray.isNotEmpty())PdfReader(contentsByteArray) else null
    private val coverpagePdf = PdfReader(R.inputStream("coverpages.pdf"))

    // This is called from a coroutine so can be made suspended if we need more control over what goes to which dispatcher.
    fun build(): ByteArray = ByteArrayOutputStream().apply { //
        logger.info("contents: ${contentsPDF?.numberOfPages ?: 0} pages")

        val doc = Document()
        val writer = PdfCopy(doc, this)

        doc.open()

        writer.addPages(coverpagePdf)
        writer.addPages(contentsPDF)

        writer.close()
        doc.close()
    }.toByteArray()

    // if reader is null, will do nothing
    private fun PdfCopy.addPages(reader: PdfReader?){
        if(reader == null) return
        repeat(reader.numberOfPages) {pageNumber ->
            addPage(getImportedPage(reader, pageNumber + 1))
        }
    }
}