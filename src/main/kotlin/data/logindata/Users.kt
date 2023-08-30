package data.logindata

import org.jetbrains.exposed.dao.id.IntIdTable

// Define the Users table
object Users : IntIdTable() {
    val username = char("username", 44).uniqueIndex()
    val salt = binary("salt", 16)
    val hash = binary("hash", 32)
}