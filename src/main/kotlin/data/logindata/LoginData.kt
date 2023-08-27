package data.logindata

data class LoginData(val username: String, val salt: ByteArray, val hash: ByteArray)
