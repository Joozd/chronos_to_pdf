package httphandlers

import data.LoginDataRepository
import data.SessionData
import global.Values
import io.javalin.http.Context

class MainHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
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
            if(!LoginDataRepository.checkLoginDataCorrect(uid, key))
                redirect("/bad_login_data.html")

            else{

            }
        }
    }
}