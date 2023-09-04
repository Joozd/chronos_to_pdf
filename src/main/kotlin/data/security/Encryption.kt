package data.security

import org.slf4j.LoggerFactory
import utils.base64Decoder
import utils.base64Encoder
import java.security.InvalidKeyException
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.absoluteValue

object Encryption {
    private val logger = LoggerFactory.getLogger(this::class.java)
    /**
     * Encrypts the given data using the Base64 encoded key.
     *
     * @param data The data to be encrypted as ByteArray.
     * @param base64Key The Base64 encoded key for encryption.
     * @return The encrypted data.
     */

    private fun encryptData(data: ByteArray, base64Key: String): ByteArray {
        // 1. Decode the Base64 encoded key
        val keyBytes = base64Decoder().decode(base64Key)
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // Generate a random IV
        val iv = ByteArray(12)  // Using 12 bytes for IV (standard for AES-GCM)
        SecureRandom().nextBytes(iv)

        // 2. Encrypt the data using AES-GCM with the decoded key and generated IV
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128, iv)  // 128-bit authentication tag size
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val encryptedData = cipher.doFinal(data)

        // Prepend the IV to the encrypted data
        return iv + encryptedData  // In Kotlin, you can use '+' to concatenate byte arrays
    }

    /**
     * Encrypts the given data using the Base64 encoded key.
     *
     * @param data The data to be encrypted as ByteArray.
     * @param base64Key The Base64 encoded key for encryption.
     * @return The encrypted data.
     */
    fun encryptData(data: String, base64Key: String): ByteArray =
        encryptData(data.toByteArray(Charsets.UTF_8), base64Key)

    /**
     * Decrypts the given encrypted data using the Base64 encoded key.
     *
     * @param encryptedDataWithIv The encrypted data as ByteArray.
     * @param base64Key The Base64 encoded key for decryption.
     * @return The decrypted data.
     */
    fun decryptData(encryptedDataWithIv: ByteArray, base64Key: String): ByteArray? {
        try {
            // 1. Decode the Base64 encoded key
            val keyBytes = base64Decoder().decode(base64Key)
            val secretKey = SecretKeySpec(keyBytes, "AES")

            // Extract the IV (Initialization Vector) from the start of the encrypted data
            val iv = encryptedDataWithIv.sliceArray(0..11)  // Assuming a 12-byte IV (standard for AES-GCM)
            val encryptedData = encryptedDataWithIv.sliceArray(12..< encryptedDataWithIv.size) // this means `12 until encryptedDataWithIv.size` but compiler said this is better

            // 2. Decrypt the data using AES-GCM with the decoded key and IV
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(128, iv)  // 128-bit authentication tag size
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            return cipher.doFinal(encryptedData)
        }
        catch (ike: InvalidKeyException){
            logger.info("InvalidKeyException Bad base64 key: $base64Key")
            return null
        }
        catch(badKey: AEADBadTagException){
            logger.warn("AEADBadTagException: Bad key used")
            return null
        }
        catch (bpe: BadPaddingException){
            logger.warn("BadPaddingException: Bad key used / ${bpe.stackTraceToString()}")
            return null
        }

    }

    /**
     * Decrypts the given encrypted data using the Base64 encoded key.
     *
     * @param encryptedDataWithIv The encrypted data as ByteArray.
     * @param base64Key The Base64 encoded key for decryption.
     * @return The decrypted data.
     */
    fun decryptDataToString(encryptedDataWithIv: ByteArray, base64Key: String): String? =
        decryptData(encryptedDataWithIv, base64Key)?.toString(Charsets.UTF_8)

    /**
     * Generates a 256-bit key
     * @param sizeBytes The size of the key, in bytes. Default 32 (256 bits)
     */
    fun generateSecureRandomData(sizeBytes: Int = 32) =
        ByteArray(sizeBytes).apply{
            SecureRandom().nextBytes(this)
        }

    fun generateBase64Key(sizeBytes: Int = 32) =
        base64Encoder().encodeToString(generateSecureRandomData(sizeBytes))

    fun generateUserName(length: Int = 16): String =
        StringBuilder(length).apply {
            repeat(length) {
                append(getRandomChar())
            }
        }.toString()

    private fun getRandomChar(): Char =
        allowedCharacters[SecureRandom().nextInt().absoluteValue % (allowedCharacters.size - 1)]
    private val allowedCharacters = ('a'..'z') + ( 'A'..'Z') + ('0'..'9')
}