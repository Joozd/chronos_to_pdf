package httphandlers

import data.LoginDataRepository
import data.SessionData
import email.Mailer
import global.Values
import io.javalin.http.Context
import kotlinx.coroutines.launch
import utils.extensions.ioScope

class MainHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            val userWantsReset = queryParam(Values.RESET)

            sessionData.apply{
                username = queryParam(Values.UID)
                userBase64Key = queryParam(Values.KEY)
            }
            val uid = sessionData.username
            val key = sessionData.userBase64Key

            if (uid == null || key == null){ /* || uid !in knownUIDs || key not OK; maybe through another page for not OK data */
                redirect("/signup.html")
                return
            }
            if (userWantsReset == "true"){
                val emailAddress = LoginDataRepository.checkResetData(uid, key)
                if(emailAddress == null) { // returns true for valid login data, else it is bad login data
                    // TODO make this a bit nicer error, might be that the data expired. Redirect to a page with explanation and a link to root for starting over.
                    redirect("/bad_login_data.html")
                }
                else {
                    redirect("/confirmation.html")
                    ioScope?.launch {
                        val newLoginData = LoginDataRepository.createNewUser(emailAddress)
                        Mailer().sendNewUserMail(emailAddress, newLoginData)
                    }
                }
                return
            }

            if(!loggedIn(sessionData))
                redirect("/bad_login_data.html")

            else{
                redirect("/upload.html")
            }
        }
    }
}