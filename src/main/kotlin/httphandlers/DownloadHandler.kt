package httphandlers

import data.SessionData
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
        val inputStream = sessionData.downloadableFile?.inputStream()
        if(inputStream == null){
            logger.info("inputStream = null from $sessionData")
            status(500)
            return
        }
        result(inputStream)
            .contentType("application/pdf") // "application/pdf"
            .header("Content-Disposition", "attachment; filename=TEST_FILE.pdf")
    }
}