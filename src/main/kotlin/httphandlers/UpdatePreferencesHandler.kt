package httphandlers

import data.LoginDataRepository
import data.PreferencesData
import data.SessionData
import data.UserPrefsRepository
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


        // Save the preferences to db
        val uid = sessionData.username
        val key = sessionData.userBase64Key

        try{
            require(LoginDataRepository.checkLoginDataCorrect(uid!!, key!!)) // also fails if uid or key is null, which is what we want
            UserPrefsRepository.Session(uid, key).savePreferencesData(preferences)
        }
        catch(e: Throwable) {
            logger.warn("Bad login data received: $uid:$key")
            logger.warn(e.stackTraceToString())
            status(500)
            return
        }
    }
}