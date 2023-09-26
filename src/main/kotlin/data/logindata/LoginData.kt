package data.logindata

data class LoginData(val username: String, val salt: ByteArray, val hash: ByteArray){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true         // same object
        if (other !is LoginData) return false   // different class

        // compare contents
        return username == other.username
                && salt.contentEquals(other.salt)
                && hash.contentEquals(other.hash)
    }
    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + hash.contentHashCode()
        return result
    }
}
