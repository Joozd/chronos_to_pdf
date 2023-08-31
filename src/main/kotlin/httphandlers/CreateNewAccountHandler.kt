package httphandlers

import data.LoginDataRepository
import data.SessionData
import email.Mailer
import io.javalin.http.Context
import kotlinx.coroutines.launch
import utils.extensions.ioScope

/**
 * Receive a POST to email user
 */
class CreateNewAccountHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            sessionData.emailAddress?.let { address ->
                ioScope?.launch {
                    val newLoginData = LoginDataRepository.createNewUser(address)
                    Mailer().sendNewUserMail(address, newLoginData)
                }
                redirect("/confirmation.html")
                return
            }
        }
        // If we get here, user did something wrong. Send him back to start.
        ctx.redirect("/")
    }
}