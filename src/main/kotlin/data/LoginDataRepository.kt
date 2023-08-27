package data

import SecureHasher
import data.logindata.LoginData
import data.logindata.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object LoginDataRepository {
    fun insertUser(user: LoginData) {
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
}