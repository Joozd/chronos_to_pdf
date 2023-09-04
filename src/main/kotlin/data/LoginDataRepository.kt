package data

import SecureHasher
import data.logindata.LoginData
import data.logindata.LoginWithKey
import data.logindata.User
import data.security.Encryption
import org.jetbrains.exposed.sql.transactions.transaction
import utils.base64Encoder

object LoginDataRepository {
    /**
     * Checks if login data for a combination of [uid] and [base64key] is correct.
     */
    fun checkLoginDataCorrect(uid: String, base64key: String): Boolean{
        val user = getUserByUsername(uid) ?: return false
        // check if the stored hash is the same as calculated hash
        return SecureHasher.hashKeyWithSalt(base64key, uid, user.salt).contentEquals(user.hash)
    }

    /**
     * Checks if a user exists
     */
    fun checkIfEmailUsed(email: String) = checkIfUserExists(emailToUid(email))


    /**
     * UID is just the Base64 encoded hashed and peppered lowercase email address
     */
    private fun emailToUid(email: String): String {
        val emailLowercase = email.lowercase() // all lowercase to avoid duplicates when user uses capitals
        val hash = SecureHasher.hashEmailAddress(emailLowercase)
        return base64Encoder().encodeToString(hash)
    }

    /**
     * Generate a username+password combination and stores its salt+hash.
     * If a user already exists, it overwrites that user's data.
     */
    fun createNewUser(emailAddress: String): LoginWithKey{
        // Generate a unique username
        val uid = emailToUid(emailAddress)

        val key = Encryption.generateSecureRandomData()
        val salt = SecureHasher.generateSalt()

        val hash = SecureHasher.hashKeyWithSalt(key, uid, salt)

        val base64key = base64Encoder().encodeToString(key)

        insertUser(LoginData(uid, salt, hash))
        FlightsDataRepository.createNewDataForUser(LoginWithKey(uid, base64key)) // using base64key, so we can just use the same function in FlightsDataRepository

        return LoginWithKey(uid, base64key)
    }


    /**
     * Check if a username already exists
     */
    private fun checkIfUserExists(uid: String): Boolean = getUserByUsername(uid) != null

    /**
     * Insert a new user. Overwrites existing user.
     */
    private fun insertUser(user: LoginData) {
        transaction {
            val existingUser = User.findById(user.username)
            if (existingUser != null){
                existingUser.salt = user.salt
                existingUser.hash = user.hash
            }
            else {
                User.new(user.username) {
                    salt = user.salt
                    hash = user.hash
                }
            }
        }
    }

    /**
     * Gets a user's name, salt and hash in a [LoginData] instance
     */
    private fun getUserByUsername(username: String): LoginData? {
        return transaction {
            User.findById(username)?.toLoginData()
        }
    }




}