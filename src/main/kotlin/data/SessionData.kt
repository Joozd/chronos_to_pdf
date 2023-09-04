package data

import nl.joozd.joozdlogcommon.BasicFlight
import utils.TemporaryResultObject

/**
 * This holds all data for a session. Separate object so tasks can access it even after they can no longer access
 * Session, as long as they have a reference to the instance.
 */
data class SessionData(
    /**
     * true if there is a download waiting for the user
     */
    var downloadReady: Boolean = false,

    /**
     * This is the entry point for the user's data in the database.
     */
    var username: String? = null,

    /**
     * user's key. Needed to en/decrypt data in the database.
     */
    var userBase64Key: String? = null,

    /**
     * User's flights. Can be filled from database, from uploaded files ro both, and can be saved to database.
     */
    var flights: List<BasicFlight> = emptyList(),

    /**
     * a Temporary Result Object, for dev and debugging
     */
    var temporaryResultObject: TemporaryResultObject? = null,

    /**
     * Email address for user, for account creation
     */
    var emailAddress: String? = null
)