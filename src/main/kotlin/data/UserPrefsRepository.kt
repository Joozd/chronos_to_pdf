package data

import data.logindata.LoginWithKey
import data.security.Encryption
import data.userprefs.EncryptedUserPrefs
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import utils.jsonToObject
import java.time.Instant

object UserPrefsRepository {
    /**
     * Insert Encrypted Data for user. Overwrites old data that may have been stored.
     * Updates [EncryptedUserPrefs.lastAccessed]
     */
    private fun createOrUpdateUserPrefsForUser(username: String, encryptedData: ByteArray) {
        transaction {
            // get or create EncryptedUserData
            val existingUserPrefs = EncryptedUserPrefs.findById(username)

            if (existingUserPrefs != null) {
                existingUserPrefs.lastAccessed = Instant.now().epochSecond
                existingUserPrefs.encryptedData = ExposedBlob(encryptedData)
            }
            else {
                EncryptedUserPrefs.new(username) {
                    lastAccessed = Instant.now().epochSecond
                    this.encryptedData = ExposedBlob(encryptedData)
                }
            }
        }
    }


    /**
     * Retrieve Encrypted Data for user. Overwrites old data that may have been stored.
     * Updates [EncryptedUserPrefs.lastAccessed]
     */
    private fun getEncryptedDataForUser(username: String): ByteArray? =
        transaction {
            EncryptedUserPrefs.findById(username)?.let {
                it.lastAccessed = Instant.now().epochSecond
                //returns encryptedData.bytes
                it.encryptedData.bytes
            }
        }

    /**
     * Insert [preferencesData] for user. Overwrites old data that may have been stored.
     * NOTE this does not check if a users login data is correct.
     */
    private fun saveDataForUSer(loginWithKey: LoginWithKey, preferencesData: PreferencesData){
        val encryptedData = Encryption.encryptData(preferencesData.toJson(), loginWithKey.base64Key)
        createOrUpdateUserPrefsForUser(loginWithKey.uid, encryptedData)
    }

    /**
     * Get PreferencesData for user. Null if user not found or key incorrect.
     */
    private fun getAndDecryptDataForUser(loginWithKey: LoginWithKey): PreferencesData? {
        val encryptedUserPrefs = getEncryptedDataForUser(loginWithKey.uid) ?: return null
        val json = Encryption.decryptDataToString(encryptedUserPrefs, loginWithKey.base64Key) ?: return null // maybe throw an exception instead of returning null?
        return jsonToObject(json)
    }

    /**
     * Create new entry for user. Removes old data for that user.
     */
    fun createNewDataForUser(loginWithKey: LoginWithKey) =
        saveDataForUSer(loginWithKey, PreferencesData.DEFAULT)

    class Session(private val loginWithKey: LoginWithKey){
        constructor (id: String, base64Key: String): this(LoginWithKey(id, base64Key))
        /**
         * Create new entry for user. Removes old data for that user.
         */
        fun savePreferencesData(preferencesData: PreferencesData) =
            saveDataForUSer(loginWithKey, preferencesData)

        fun getPreferencesData() = getAndDecryptDataForUser(loginWithKey)
    }
}