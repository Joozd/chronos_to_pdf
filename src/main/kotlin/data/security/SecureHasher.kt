import data.Config
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.Base64

object SecureHasher {
    //shortcut for cleaner coded
    private val config get() = Config.getInstance()

    /**
     * Generate a random salt using SecureRandom.
     * This salt can be stored alongside hashed values and used for future hash verifications.
     * @return A randomly generated salt as ByteArray.
     */
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        return ByteArray(16).apply { random.nextBytes(this) }
    }

    /**
     * Hashes a given key using the provided username and salt.
     * The combination of username and salt ensures unique hashes even for duplicate keys across users.
     * @param key The key to be hashed as ByteArray.
     * @param username The username to be combined with the salt.
     * @param salt The salt to be combined with the key.
     * @return An SHA-256 hash of the key combined with the username and salt.
     * @throws NoSuchAlgorithmException if the algorithm "SHA-256" is not available.
     */
    fun hashKeyWithSalt(key: ByteArray, username: String, salt: ByteArray): ByteArray {
        val usernameBytes = username.toByteArray(StandardCharsets.UTF_8)
        val combined = key + usernameBytes + salt
        return sha256Hash(combined)
    }

    /**
     * Hashes a given key using the provided username and salt.
     * The combination of username and salt ensures unique hashes even for duplicate keys across users.
     * @param base64Key The key to be hashed as ByteArray, as a Base64 string
     * @param username The username to be combined with the salt.
     * @param salt The salt to be combined with the key.
     * @return An SHA-256 hash of the key combined with the username and salt.
     * @throws NoSuchAlgorithmException if the algorithm "SHA-256" is not available.
     */
    fun hashKeyWithSalt(base64Key: String, username: String, salt: ByteArray): ByteArray {
        val key = Base64.getDecoder().decode(base64Key)
        return hashKeyWithSalt(key, username, salt)
    }

    /**
     * Hash email address to username. Not secure, only uses pepper.
     */
    fun hashEmailAddress(email: String): ByteArray{
        val combined = email + config["pepper"]
        return sha256Hash(combined.toByteArray(Charsets.UTF_8))
    }

    /**
     * SHA-256 hash of [bytes]
     */
    private fun sha256Hash(bytes: ByteArray): ByteArray=
        MessageDigest.getInstance("SHA-256").digest(bytes)

}
