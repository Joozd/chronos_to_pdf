package httphandlers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import data.LoginDataRepository
import data.SessionData
import io.javalin.http.Context
import io.javalin.http.bodyAsClass

class CheckIfEmailExistsHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        val email = try { bodyAsClass<EmailPayload>().email }
        catch(e: Throwable) {
            logger.warn("Bad data received: ${body()}")
            logger.warn(e.stackTraceToString())
            status(400)
            return
        }
        val exists = LoginDataRepository.checkIfEmailUsed(email)
        sessionData.emailAddress = email

        json(exists)
    }

    private data class EmailPayload @JsonCreator constructor(
        @JsonProperty("email") val email: String
    )
}