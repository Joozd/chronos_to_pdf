package httphandlers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import data.LoginDataRepository
import data.SessionData
import io.javalin.http.Context
import io.javalin.http.bodyAsClass

class CheckIfEmailExistsHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            val email = try { bodyAsClass<EmailPayload>().email }
            catch(e: Throwable) {
                logger.warn("Bad data received: ${ctx.body()}")
                logger.warn(e.stackTraceToString())
                ctx.status(400)
                return
            }
            val exists = LoginDataRepository.checkIfEmailUsed(email)
            sessionData.emailAddress = email
            logger.info("SessionData: $sessionData")

            ctx.json(exists)
        }
    }

    private data class EmailPayload @JsonCreator constructor(
        @JsonProperty("email") val email: String
    )
}