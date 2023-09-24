package data

import data.flightsdata.EncryptedUserData
import data.logindata.LoginWithKey
import data.security.Encryption
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.joozdlogimporter.interfaces.FileImporter
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import utils.extensions.distinctTimes
import utils.extensions.postProcess
import java.time.Instant

/**
 * FlightData is saved as a Joozdlog CSV file, stored under a user's name and encrypted with user's key.
 */
object FlightsDataRepository {
    /**
     * Insert Encrypted Data for user. Overwrites old data that may have been stored.
     * Updates [EncryptedUserData.lastAccessed]
     */
    private fun createOrUpdateEncryptedDataForUser(username: String, data: ByteArray) {
        transaction {
            // get or create EncryptedUserData
            val existingUser = EncryptedUserData.findById(username)
            if (existingUser != null) {
                existingUser.encryptedData = ExposedBlob(data)
            }
            else {
                EncryptedUserData.new(username) {
                    lastAccessed = Instant.now().epochSecond
                    encryptedData = ExposedBlob(data)
                }
            }
        }
    }



    /**
     * Retrieve Encrypted Data for user. Overwrites old data that may have been stored.
     * Updates [EncryptedUserData.lastAccessed]
     */
    private fun getEncryptedDataForUser(username: String): ByteArray? =
        transaction {
            EncryptedUserData.findById(username)?.let {
                it.lastAccessed = Instant.now().epochSecond
                //returns encryptedData.bytes
                it.encryptedData.bytes
            }
        }

    /**
     * Inserts data for user. Note that FlightID is not used and will always be -1.
     */
    private fun insertDataForUser(username: String, base64Key: String, flights: Collection<BasicFlight>){
        val csv = flights.joinToString("\n") { it.toCsv() }
        val encryptedCSV = Encryption.encryptData(csv, base64Key)
        createOrUpdateEncryptedDataForUser(username, encryptedCSV)
    }

    fun insertDataForUser(loginWithKey: LoginWithKey, flights: Collection<BasicFlight>) =
        insertDataForUser(loginWithKey.uid, loginWithKey.base64Key, flights)

    /**
     * Gets data for user. Note that FlightID is not used and will always be -1.
     */
    fun getDataForUser(username: String, base64Key: String): Collection<BasicFlight>? {
        val encryptedUserData = getEncryptedDataForUser(username) ?: return null
        val decryptedData = Encryption.decryptDataToString(encryptedUserData, base64Key) ?: return null

        //parse decrypted csv lines to flights and return them
        return decryptedData.lines().filter { it.isNotBlank() }.map { BasicFlight.ofCsv(it) }
    }

    fun getDataForUser(loginWithKey: LoginWithKey) =
        getDataForUser(loginWithKey.uid, loginWithKey.base64Key)

    /**
     * Create new entry for user. Removes old data for that user.
     */
    fun createNewDataForUser(loginWithKey: LoginWithKey) = insertDataForUser(loginWithKey, emptyList())


    class Session(username: String, base64Key: String){
        private val loginWithKey = LoginWithKey(username, base64Key)

        /**
         * Add flights from [importers] that are not yet in the database.
         */
        fun addFlightsFromFiles(importers: Collection<FileImporter>, preferencesData: PreferencesData): List<BasicFlight>{
            val knownFlights = getDataForUser(loginWithKey) ?: return emptyList() // maybe throw an error about not being logged in?
            val flights = importers. map { it.getFile().getFlights() ?: emptyList() }
                .flatten()

            val result = (flights + knownFlights) // new flights go  first, so they are saved (in case my algorithm gets improved)
                .distinctTimes()     // flights with same time out and time in are the same flights. This does allow for only one simulator duty per day!
                .postProcess(preferencesData)

            insertDataForUser(loginWithKey, result)

            return result.sortedBy { it.timeOut }
        }
    }
}