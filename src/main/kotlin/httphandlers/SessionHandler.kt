package httphandlers

import data.LoginDataRepository
import data.SessionData
import io.javalin.http.Context
import io.javalin.http.Handler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.extensions.getSessionData

/**
 * a [Handler] with access to [SessionData]
 */
abstract class SessionHandler: Handler {
    protected val logger: Logger get() = LoggerFactory.getLogger(this::class.java)
    override fun handle(ctx: Context) {
        ctx. handleWithSessionData(ctx.getSessionData())
    }
    abstract fun Context.handleWithSessionData(sessionData: SessionData)

    /**
     * check if [sessionData] contains valid login data
     */
    fun loggedIn(sessionData: SessionData): Boolean{
        val uid = sessionData.username ?: return false
        val key = sessionData.userBase64Key ?: return false

        return LoginDataRepository.checkLoginDataCorrect(uid, key)
    }
}