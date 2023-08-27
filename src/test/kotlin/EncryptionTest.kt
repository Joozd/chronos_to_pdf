import data.security.Encryption
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

object EncryptionTest {
    private val testData = "Banaan12345"
    private val testKey = Encryption.generateBase64Key()
    private val wrongKey = Encryption.generateBase64Key()

    @Test
    fun test_generateSecureRandomKey(){
        val keySize = 32
        val key = Base64.getDecoder().decode(testKey)

        assertEquals(keySize, key.size)
        assert(!ByteArray(keySize).contentEquals(key)) // check testKey is not just an empty 32-byte ByteArray
        assert(!wrongKey.contentEquals(testKey)) // assert wrongKey is not the same as testkey. This part can fail on average once every 2^256 tests.
        assertEquals(Base64.getEncoder().encodeToString(key), testKey)
    }

    @Test
    fun test_encryptDecrypt(){
        val encrypted = Encryption.encryptData(data = testData, base64Key = testKey)
        println(1)

        // Check at least something has happened to the testData
        assert(!testData.toByteArray(Charsets.UTF_8).contentEquals(encrypted))

        // Check decrypting with correct key gives correct result
        assertEquals(testData, Encryption.decryptDataToString(encrypted, testKey))

        // Check decrypting with bad key gives null
        println("The next test might log a Bad Key warning:")
        assertEquals(null, Encryption.decryptDataToString(encrypted, wrongKey))
        println("Test performed. Any logs after this are not from the BAd Key test.")
    }
}