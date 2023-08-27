import data.LoginDataRepository
import data.flightsdata.EncryptedUserDataTable
import data.logindata.Users
import data.security.Encryption
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DBTests {
    @BeforeAll
    fun setupDatabaseConnection() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(EncryptedUserDataTable)
        }
        // Optionally create tables or other setup activities here.
    }

    @BeforeEach
    fun setup() {
        transaction {
            // Populate tables with test data if needed here
        }
    }

    @AfterEach
    fun cleanup() {
        // drop tables, or do other cleanup activities here.
        // Make sure everything gets recreated every time so tests do not depend on the previous test that runs.
        transaction {
            Users.deleteAll() // Clear Users table
            EncryptedUserDataTable.deleteAll() // clear EncryptedUserDataTable
        }
    }

    @Test
    fun testCreateLoginData() {
        /*
         * Test creation, insertion and retrieval(checking) of login data
         */
        val loginData = LoginDataRepository.generateUser() // generate user, store in DB
        //check correct data is correct
        assert(LoginDataRepository.checkLoginDataCorrect(loginData.uid, loginData.base64Key))

        //check incorrect data is incorrect
        assert(!LoginDataRepository.checkLoginDataCorrect(loginData.uid, Encryption.generateBase64Key()))
        assert(!LoginDataRepository.checkLoginDataCorrect(Encryption.generateUserName(), loginData.base64Key))
    }

    // Add other tests as needed
}