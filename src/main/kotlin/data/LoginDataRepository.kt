package data

import SecureHasher
import data.logindata.LoginData
import data.logindata.LoginWithKey
import data.logindata.Users
import data.security.Encryption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object LoginDataRepository {
    private fun insertUser(user: LoginData) {
        transaction {
            Users.insert {
                it[username] = user.username
                it[salt] = user.salt
                it[hash] = user.hash
            }
        }
    }

    fun getUserByUsername(username: String): LoginData? {
        return transaction {
            Users.select { Users.username eq username }
                .singleOrNull()
                ?.let {
                    LoginData(
                        it[Users.username],
                        it[Users.salt],
                        it[Users.hash]
                    )
                }
        }
    }

    fun checkLoginDataCorrect(uid: String, base64key: String): Boolean{
        val user = getUserByUsername(uid) ?: return false
        // check if the stored hash is the same as calculated hash
        return SecureHasher.hashKeyWithSalt(base64key, uid, user.salt).contentEquals(user.hash)
    }

    /**
     * Check if a username already exists
     */
    private fun checkIfUserExists(uid: String): Boolean = getUserByUsername(uid) != null

    /**
     * Checks if a user exists
     */
    fun checkIfEmailUsed(email: String) = checkIfUserExists(emailToUid(email))


    /**
     * UID is just the Base64 encoded hashed and peppered lowercase email address
     */
    fun emailToUid(email: String): String {
        val emailLowercase = email.lowercase() // all lowercase to avoid duplicates when user uses capitals
        val hash = SecureHasher.hashEmailAddress(emailLowercase)
        return Base64.getEncoder().encodeToString(hash)
    }

    /**
     * Generate a username+password combination and stores its salt+hash.
     * TODO: Check if user doesn't exist yet (edge case)
     */
    fun generateUser(): LoginWithKey{
        // Generate a unique username
        var userName: String
        do
            userName = Encryption.generateUserName()
        while(checkIfUserExists(userName))

        val key = Encryption.generateSecureRandomData()
        val salt = Encryption.generateSecureRandomData(16)

        val hash = SecureHasher.hashKeyWithSalt(key, userName, salt)

        val base64key = Base64.getEncoder().encodeToString(key)

        insertUser(LoginData(userName, salt, hash))

        return LoginWithKey(userName, base64key)
    }
}