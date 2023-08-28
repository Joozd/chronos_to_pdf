package data

import data.flightsdata.EncryptedUserData
import data.logindata.LoginWithKey
import data.security.Encryption
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.joozdlogimporter.CsvImporter
import nl.joozd.joozdlogimporter.supportedFileTypes.CompleteLogbookFile
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

/**
 * FlightData is saved as a Joozdlog CSV file, stored under a user's name and encrypted with user's key.
 */
object FlightsDataRepository {
    /**
     * Insert Encrypted Data for user. Overwrites old data that may have been stored.
     * Updates [EncryptedUserData.lastAccessed]
     */
    private fun insertEncryptedDataForUser(username: String, data: ByteArray) {
        transaction {
            EncryptedUserData.new(username) {
                lastAccessed = Instant.now().epochSecond
                encryptedData = ExposedBlob(data)
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
    fun insertDataForUser(username: String, base64Key: String, flights: Collection<BasicFlight>){
        val csv = BasicFlight.CSV_IDENTIFIER_STRING + "\n" + flights.joinToString("\n") { it.toCsv() }
        val encryptedCSV = Encryption.encryptData(csv, base64Key)
        insertEncryptedDataForUser(username, encryptedCSV)
    }

    fun insertDataForUser(loginWithKey: LoginWithKey, flights: Collection<BasicFlight>) =
        insertDataForUser(loginWithKey.uid, loginWithKey.base64Key, flights)

    /**
     * Gets data for user. Note that FlightID is not used and will always be -1.
     */
    fun getDataForUser(username: String, base64Key: String): Collection<BasicFlight>?{
        val encryptedUserData = getEncryptedDataForUser(username) ?: return null
        val decryptedData = Encryption.decryptDataToString(encryptedUserData, base64Key) ?: return null
        val file = CsvImporter(decryptedData.lines()).getFile() as? CompleteLogbookFile ?: return null
        return file.extractCompletedFlights().flights
    }

    fun getDataForUser(loginWithKey: LoginWithKey) =
        getDataForUser(loginWithKey.uid, loginWithKey.base64Key)
}