package httphandlers

import data.SessionData
import io.javalin.http.Context

/**
 * Will supply "done" to true when done, or false when not done.
 */
class StatusHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        val isDone = sessionData.downloadReady
        json(mapOf("done" to isDone))
    }
}