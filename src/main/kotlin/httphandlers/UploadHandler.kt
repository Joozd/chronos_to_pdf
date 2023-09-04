package httphandlers

import data.SessionData
import global.StatusKeeper
import global.Values
import io.javalin.http.Context
import kotlinx.coroutines.launch
import parsing.MockParser
import utils.extensions.defaultScope

class UploadHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            if (!loggedIn(sessionData)) {
                redirect("/bad_login_data.html")
                return
            }

            val uploadedFiles = uploadedFiles()

            // Session might not be available when the parser is done, but this object will be.
            val statusKeeper = sessionAttribute(Values.STATUS_KEEPER) ?: StatusKeeper().also{ sessionAttribute(Values.STATUS_KEEPER, it)}

            val scope = defaultScope
            if(scope == null)
                ctx.status(500)
            else{
                scope.launch {
                    MockParser().parse(uploadedFiles.map { it.filename() })
                    statusKeeper.downloadReady = true
                }
            }

            ctx.redirect("/wait.html")
        }
    }
}