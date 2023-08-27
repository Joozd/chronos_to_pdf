package httphandlers

import data.LoginDataRepository
import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler

class MainHandler: Handler {
    override fun handle(ctx: Context) {
        with(ctx) {
            val uid: String? = queryParam(Values.UID)
            val key: String? = queryParam(Values.KEY)
            if (uid == null || key == null){ /* || uid !in knownUIDs || key not OK; maybe through another page for not OK data */
                redirect("/signup.html")
                return
            }
            if(!LoginDataRepository.checkLoginDataCorrect(uid, key))
                redirect("/bad_login_data.html")

            else{
                sessionAttribute(Values.UID, uid)
            }

        }
    }
}