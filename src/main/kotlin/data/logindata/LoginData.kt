package data.logindata

import data.security.Encryption
import java.security.SecureRandom

data class LoginData(val username: String, val salt: ByteArray, val hash: ByteArray){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginData

        if (username != other.username) return false
        if (!salt.contentEquals(other.salt)) return false
        if (!hash.contentEquals(other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + hash.contentHashCode()
        return result
    }
}
