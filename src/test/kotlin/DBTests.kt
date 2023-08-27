import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DBTests {
    @BeforeAll
    fun setupDatabaseConnection() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        // Optionally create tables or other setup activities here.
    }

    @BeforeEach
    fun setup() {
        // create the tables you need for testing here.
        // Make sure everything gets recreated every time so tests do not depend on the previous test that runs.
    }

    @AfterEach
    fun cleanup() {
        // drop tables, or do other cleanup activities here.
        // Make sure everything gets recreated every time so tests do not depend on the previous test that runs.
    }

    @Test
    fun testMyDatabaseFunction() {
        // Your test code here
    }

    // Add other tests as needed
}