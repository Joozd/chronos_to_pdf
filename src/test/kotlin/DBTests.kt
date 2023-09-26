import data.FlightsDataRepository
import data.LoginDataRepository
import data.PreferencesData
import data.UserPrefsRepository
import data.flightsdata.EncryptedUserDataTable
import data.logindata.Users
import data.security.Encryption
import data.userprefs.EncryptedUserPrefsTable
import helpers.generateFlights
import nl.joozd.joozdlogcommon.BasicFlight
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DBTests {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @BeforeAll
    fun setupDatabaseConnection() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(EncryptedUserDataTable)
            SchemaUtils.create(EncryptedUserPrefsTable)
        }
        // Optionally create tables or other setup activities here.
    }

    @BeforeEach
    fun setup() {
        transaction {
            // Populate tables with test data if needed here.
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


    /**
     * Test creation, insertion and retrieval(checking) of login data
     */
    @Test
    fun testCreateLoginData() {
        val loginData = LoginDataRepository.createNewUser(TEST_EMAIL_ADDRESS) // generate user, store in DB
        //check correct data is correct
        assert(LoginDataRepository.checkLoginDataCorrect(loginData.uid, loginData.base64Key))

        //check incorrect data is incorrect
        assert(!LoginDataRepository.checkLoginDataCorrect(loginData.uid, Encryption.generateBase64Key()))
        assert(!LoginDataRepository.checkLoginDataCorrect(TEST_EMAIL_ADDRESS, loginData.base64Key))
    }
    // Add other tests as needed

    /**
     * Test insertion and retrieval of flight data in encrypted DB
     */
    @Test
    fun testInsertRetrieveFlights(){
        val flights = generateFlights(2)
        val testUser = LoginDataRepository.createNewUser(TEST_EMAIL_ADDRESS)

        // test inserting flights:
        FlightsDataRepository.insertDataForUser(testUser, flights)
        val retrievedFlights = FlightsDataRepository.getDataForUser(testUser)

        assertEquals(flights, retrievedFlights)
    }

    /**
     * Test creation and retrieval of UserPreferences
     */
    @Test
    fun testUserPreferencesData(){
        val loginData = LoginDataRepository.createNewUser(TEST_EMAIL_ADDRESS) // generate user, store in DB
        val preferences1 = PreferencesData.DEFAULT
        val preferences2 = PreferencesData(
            logLanding = false,
            guessSimType = false,
            removeSimTypes = true,
            function = PreferencesData.CAPTAIN
        )

        println("DEBUG: $loginData")
        val session = UserPrefsRepository.Session(loginData)
        assertNull(session.getPreferencesData()) // no data present should give null
        session.savePreferencesData(preferences1)
        assertEquals(preferences1, session.getPreferencesData())
        session.savePreferencesData(preferences2)
        assertEquals(preferences2, session.getPreferencesData())

        val newLoginData = loginData.copy(base64Key = Encryption.generateBase64Key())
        val newSession = UserPrefsRepository.Session(newLoginData)
        assertNull(newSession.getPreferencesData())
    }


    @Test
    fun testCreateNewUserFromEmail(){
        val email = TEST_EMAIL_ADDRESS
        val unusedEmail = "still_unused@email.com"

        // Check if user doesn't exist
        assertFalse(LoginDataRepository.checkIfEmailUsed(email))

        val newUser = LoginDataRepository.createNewUser(email)

        // check that user does exist now
        assert(LoginDataRepository.checkIfEmailUsed(email))
        assertFalse(LoginDataRepository.checkIfEmailUsed(unusedEmail))

        // Check that user now has data in FlightsDataRepository
        assertEquals(emptyList(), FlightsDataRepository.getDataForUser(newUser))

        //insert a flight, so we can check it gets cleaned when overwritten
        FlightsDataRepository.insertDataForUser(newUser, listOf(BasicFlight.PROTOTYPE))

        val overwrittenUser = LoginDataRepository.createNewUser(email)

        //check user and overwrittenUser are not the same (should have new key)
        assertNotEquals(newUser, overwrittenUser)

        //check if user still marked as existing
        assert(LoginDataRepository.checkIfEmailUsed(email))

        //check if users' flights got emptied
        assertEquals(emptyList(), FlightsDataRepository.getDataForUser(overwrittenUser))

        //check old user cannot get data
        logger.info("This should cause a warning \"AEADBadTagException: Bad key used\"")
        assertNull(FlightsDataRepository.getDataForUser(newUser))
        logger.info("Warning should be above this line.")


    }

    companion object{
        private const val TEST_EMAIL_ADDRESS = "someMail@test.com"
    }
}