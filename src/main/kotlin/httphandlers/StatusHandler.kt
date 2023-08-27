package httphandlers

import data.SessionData
import global.StatusKeeper
import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler

/**
 * Will supply "done" to true when done, or false when not done.
 */
class StatusHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        val isDone = sessionData.downloadReady
        println("Checking if done ... $isDone")
        ctx.json(mapOf("done" to isDone))
    }
}