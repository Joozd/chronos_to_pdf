import data.security.Encryption
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import utils.base64Decoder
import utils.base64Encoder
import kotlin.test.assertEquals

object EncryptionTest {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val testData = "Banaan12345"
    private val testKey = Encryption.generateBase64Key()
    private val wrongKey = Encryption.generateBase64Key()

    @Test
    fun test_generateSecureRandomKey(){
        val keySize = 32
        val key = base64Decoder().decode(testKey)

        assertEquals(keySize, key.size)
        assert(!ByteArray(keySize).contentEquals(key)) // check testKey is not just an empty 32-byte ByteArray
        assert(!wrongKey.contentEquals(testKey)) // assert wrongKey is not the same as testKey. This part can fail on average once every 2^256 tests.
        assertEquals(base64Encoder().encodeToString(key), testKey)
    }

    @Test
    fun test_encryptDecrypt(){
        val encrypted = Encryption.encryptData(data = testData, base64Key = testKey)

        // Check at least something has happened to the testData
        assert(!testData.toByteArray(Charsets.UTF_8).contentEquals(encrypted))

        // Check decrypting with correct key gives correct result
        assertEquals(testData, Encryption.decryptDataToString(encrypted, testKey))

        // Check decrypting with bad key gives null
        logger.info("The next test might log a Bad Key warning:")
        assertEquals(null, Encryption.decryptDataToString(encrypted, wrongKey))
        logger.info("Test performed. Any logs after this are not from the BAd Key test.")
    }
}