package httphandlers

import data.LoginDataRepository
import data.SessionData
import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import org.slf4j.LoggerFactory

class CheckIfEmailExistsHandler: SessionHandler() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            val email = try { bodyAsClass<EmailPayload>().email }
            catch(e: Throwable) {
                logger.warn("Bad data received: ${ctx.body()}")
                ctx.status(400)
                return
            }
            val exists = LoginDataRepository.checkIfEmailUsed(email)
            sessionData.emailAddress = email

            ctx.json(exists)
        }
    }

    private data class EmailPayload(val email: String)
}