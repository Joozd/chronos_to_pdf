package httphandlers

import data.SessionData
import global.MimeTypes
import global.Values
import io.javalin.http.Context

/**
 * This is a handler that provides the built PDF that should be available.
 */
class DownloadHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        if (!sessionData.downloadReady){
            status(400).result("Bad request, session not ready. Sessiondata: $sessionData")
            return
        }
        val wantedType = queryParam(Values.DOWNLOAD_TYPE)
        // Doing this as a when statement, so I can easily add other file types. Default is PDF.
        val inputStream = when (wantedType){
            Values.CSV -> sessionData.downloadableCsvFile?.inputStream()
            else -> sessionData.downloadablePdfFile?.inputStream()

        }
        if(inputStream == null){
            logger.info("inputStream = null from $sessionData")
            status(500)
            return
        }

        val contentMimeType = when(wantedType){
            Values.CSV -> MimeTypes.CSV
            else -> MimeTypes.PDF // default, PDF
        }

        val fileName = when(wantedType){
            Values.CSV -> "logbook.csv"
            else -> "logbook.pdf" // default is pdf
        }
        result(inputStream)
            .contentType(contentMimeType) // "application/pdf"
            .header("Content-Disposition", "attachment; filename=$fileName")
    }

}
