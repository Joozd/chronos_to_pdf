package httphandlers

import data.PreferencesData
import data.SessionData
import data.UserPrefsRepository
import io.javalin.http.Context

class PrefsHandler: SessionHandler() {
    override fun Context.handleWithSessionData(sessionData: SessionData) {
        val uid = sessionData.username
        val key = sessionData.userBase64Key
        if(uid == null || key == null)
            result(PreferencesData.DEFAULT.toJson())
        else
        try {
            // bad key, bad username or no data saved will result in DEFAULT. That is OK because nothing will get broken.
            // null username and key should not happen here.
            val userPrefs = UserPrefsRepository.Session(uid, key).getPreferencesData() ?: PreferencesData.DEFAULT

            result(userPrefs.toJson()) // result of this call
        }
        catch (e: Throwable){
            logger.warn(e.stackTraceToString())
            status(500)
        }
    }
}