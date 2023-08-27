import data.security.Encryption
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GenerateUserDataTests {
    @Test
    fun test_generateUserName(){
        val length = 16
        val userName = Encryption.generateUserName(length)
        val userName2 = Encryption.generateUserName(length)
        assert(userName.isNotBlank())
        assertEquals(length, userName.length)
        assertNotEquals(userName, userName2)
    }
}