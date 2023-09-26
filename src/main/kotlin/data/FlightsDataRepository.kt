package data

import data.flightsdata.EncryptedUserData
import data.logindata.LoginWithKey
import data.security.Encryption
import nl.joozd.joozdlogcommon.AugmentedCrew
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.joozdlogimporter.interfaces.FileImporter
import nl.joozd.joozdlogimporter.supportedFileTypes.ImportedFile
import nl.joozd.joozdlogimporter.supportedFileTypes.PlannedFlightsFile
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
                existingUser.lastAccessed = Instant.now().epochSecond
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

    //TODO make this accessible only from [Session]
    fun insertDataForUser(loginWithKey: LoginWithKey, flights: Collection<BasicFlight>) =
        insertDataForUser(loginWithKey.uid, loginWithKey.base64Key, flights)

    /**
     * Gets data for user. Note that FlightID is not used and will always be -1.
     */
    private fun getDataForUser(username: String, base64Key: String): Collection<BasicFlight>? {
        val encryptedUserData = getEncryptedDataForUser(username) ?: return null
        val decryptedData = Encryption.decryptDataToString(encryptedUserData, base64Key) ?: return null

        //parse decrypted csv lines to flights and return them
        return decryptedData.lines().filter { it.isNotBlank() }.map { BasicFlight.ofCsv(it) }
    }

    //TODO make this accessible only from [Session] (breaks tests at this time)
    fun getDataForUser(loginWithKey: LoginWithKey) =
        getDataForUser(loginWithKey.uid, loginWithKey.base64Key)

    /**
     * Create new entry for user. Removes old data for that user.
     */
    //TODO make this accessible only from [Session]
    fun createNewDataForUser(loginWithKey: LoginWithKey) = insertDataForUser(loginWithKey, emptyList())


    /**
     * A session holds login data for a user, so it can access the user's data.
     */
    class Session(username: String, base64Key: String){
        private val loginWithKey = LoginWithKey(username, base64Key)

        /**
         * Add flights from [importers] that are not yet in the database.
         */
        fun addFlightsFromFiles(importers: Collection<FileImporter>, preferencesData: PreferencesData): List<BasicFlight>{
            val knownFlights = getDataForUser(loginWithKey) ?: return emptyList() // maybe throw an error about not being logged in?
            val flights = importers.map { it.getFile().extractFlights(preferencesData) }
                .flatten()

            val result = (flights + knownFlights) // new flights go  first, so they are saved (in case my algorithm gets improved)
                .distinctTimes()     // flights with same time out and time in are the same flights. This does allow for only one simulator duty per day!
                .sortedBy { it.timeOut }
                .postProcess(preferencesData)

            insertDataForUser(loginWithKey, result)

            return result
        }

        /**
         * Extracts flights.
         * Uses [preferencesData] for preprocessing
         *  (marking flights as PIC for extractors that do not support detection of rank)
         */
        private fun ImportedFile.extractFlights(preferencesData: PreferencesData): List<BasicFlight>{
            if(this is PlannedFlightsFile) return emptyList() // rosters are not supported, only monthlies and complete logbooks
            val flights = getFlights() ?: emptyList()
            return flights.map{f ->
                //isPic is, if supported, what the parser gave us; else what preferencesData gives us.
                val isPic = if(supports(ImportedFile.RANK)) f.isPIC else preferencesData.function == PreferencesData.CAPTAIN

                // augmented crew is, if supported, what the parser gave us, else its 3 crew if user is SO or 2 crew is user is Captain of FO (prom preferencesData)
                val crew: Int = if(supports(ImportedFile.RANK)) f.augmentedCrew
                else
                    if (preferencesData.function == PreferencesData.SO) AugmentedCrew.coco(takeoffLandingTimes = 0).toInt()
                    else 0

                f.copy(isPIC = isPic, augmentedCrew = crew)
            }
        }
    }
}