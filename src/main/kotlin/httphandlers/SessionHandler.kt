package httphandlers

import data.SessionData
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.extensions.getSessionData

/**
 * a [Handler] with access to [SessionData]
 */
abstract class SessionHandler: Handler {
    override fun handle(ctx: Context) {
        handleWithSessionData(ctx, ctx.getSessionData())
    }
    abstract fun handleWithSessionData(ctx: Context, sessionData: SessionData)
}