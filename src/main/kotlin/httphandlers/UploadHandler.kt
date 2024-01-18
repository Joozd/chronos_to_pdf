package httphandlers

import data.FlightsDataRepository
import data.SessionData
import io.javalin.http.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nl.joozd.joozdlogimporter.JoozdlogImporter
import nl.joozd.joozdlogimporter.SupportedMimeTypes
import nl.joozd.pdflogbookbuilder.PdfLogbookBuilder
import pdf.Logbook
import utils.extensions.defaultScope
import utils.extensions.ioScope
import utils.extensions.prepareForLogbook
import utils.extensions.toCsvFile
import java.io.ByteArrayOutputStream

class UploadHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        if (!loggedIn(sessionData)) {
            redirect("/bad_login_data.html")
            return
        }

        val uploadedFiles = uploadedFiles()

        val session = FlightsDataRepository.Session(
            sessionData.username ?: error("Not logged in"),
            sessionData.userBase64Key ?: error("Not logged in")
        )
        val importers = uploadedFiles.mapNotNull {
            val mimeType = when (it.extension()) {
                ".txt" -> SupportedMimeTypes.MIME_TYPE_TEXT
                ".csv" -> SupportedMimeTypes.MIME_TYPE_CSV
                ".pdf" -> SupportedMimeTypes.MIME_TYPE_PDF
                else -> return@mapNotNull null
            }
            JoozdlogImporter.ofInputStream(it.content(), mimeType)
        }
        val flights = session.addFlightsFromFiles(importers, sessionData.preferences).prepareForLogbook(sessionData.preferences)

        // this part can be done async as nothing from the session is needed anymore?
        // The sessionData object is not part of the session so should stay available.
        ioScope?.launch {
            logger.info("Starting logbook creation")
            // Build logbook content and put it in a ByteArray
            val logbookContentBytes =
                if (flights.isEmpty()) ByteArray(0)
                else ByteArrayOutputStream().apply {
                    PdfLogbookBuilder(flights).buildToOutputStream(this)
                }.toByteArray()

            // Logbook combines the logbookContentBytes with the cover pages from Resources.
            val pdfTask = async{
                sessionData.downloadablePdfFile = Logbook(logbookContentBytes).build()
                logger.info("PDF Logbook created")
            }
            val csvTask = async {
                sessionData.downloadableCsvFile = flights.toCsvFile()
                logger.info("CSV Logbook created")
            }
            pdfTask.await()
            csvTask.await()

            // signal that the logbook is ready
            sessionData.downloadReady = true
            logger.info("downloadReady = true")
        }

        redirect("/wait.html")
    }
}