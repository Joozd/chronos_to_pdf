package httphandlers

import data.PreferencesData
import data.SessionData
import io.javalin.http.Context
import io.javalin.http.bodyAsClass

class UpdatePreferencesHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        logger.info("DEBUG: BODY: ${body()}")

        val preferences = try { bodyAsClass<PreferencesData>() }
        catch(e: Throwable) {
            logger.warn("Bad data received: ${body()}")
            logger.warn(e.stackTraceToString())
            status(400)
            return
        }
        sessionData.preferences = preferences
        logger.info("DEBUG: Preferences received = $preferences")
    }
}