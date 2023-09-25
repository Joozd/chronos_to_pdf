package httphandlers

import data.FlightsDataRepository
import data.SessionData
import io.javalin.http.Context
import nl.joozd.joozdlogimporter.JoozdlogImporter
import nl.joozd.joozdlogimporter.SupportedMimeTypes
import nl.joozd.pdflogbookbuilder.PdfLogbookBuilder
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
        val flights = session.addFlightsFromFiles(importers, sessionData.preferences)

        val outputStream = ByteArrayOutputStream()

        PdfLogbookBuilder(flights).buildToOutputStream(outputStream)
        sessionData.downloadableFile = outputStream.toByteArray()
        sessionData.downloadReady = true

        redirect("/wait.html")
    }
}