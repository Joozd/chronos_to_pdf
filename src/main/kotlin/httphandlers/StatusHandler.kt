package httphandlers

import global.StatusKeeper
import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler

/**
 * Will supply "done" to true when done, or false when not done.
 */
class StatusHandler: Handler {
    override fun handle(ctx: Context) {
        val isDone = ctx.sessionAttribute<StatusKeeper>(Values.STATUS_KEEPER)?.downloadReady ?: false
        println("Checking if done ... $isDone")
        ctx.json(mapOf("done" to isDone))
    }
}