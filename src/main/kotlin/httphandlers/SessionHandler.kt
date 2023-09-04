package httphandlers

import data.SessionData
import io.javalin.http.Context
import io.javalin.http.Handler
import org.slf4j.LoggerFactory
import utils.extensions.getSessionData

/**
 * a [Handler] with access to [SessionData]
 */
abstract class SessionHandler: Handler {
    protected val logger get() = LoggerFactory.getLogger(this::class.java)
    override fun handle(ctx: Context) {
        handleWithSessionData(ctx, ctx.getSessionData())
    }
    abstract fun handleWithSessionData(ctx: Context, sessionData: SessionData)
}