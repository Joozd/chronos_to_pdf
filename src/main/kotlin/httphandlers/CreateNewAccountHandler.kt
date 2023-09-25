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
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        sessionData.emailAddress?.let { address ->
            if (LoginDataRepository.checkIfEmailUsed(address)) {
                ioScope?.launch {
                    val resetData = LoginDataRepository.createResetData(address)
                    Mailer().sendResetMail(address, resetData)
                }
                redirect("/confirmation_reset.html")
            } else {
                ioScope?.launch {
                    val newLoginData = LoginDataRepository.createNewUser(address)
                    Mailer().sendNewUserMail(address, newLoginData)
                }
                redirect("/confirmation.html")
            }
            return
        }

        // If we get here, user did something wrong. Send him back to start.
        logger.warn("something went wrong aub / sessionData: $sessionData")
        redirect("/")
    }
}