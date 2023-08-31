package data.logindata

import org.jetbrains.exposed.dao.id.IdTable

// Define the Users table
object Users: IdTable<String>() {
    override val id = char("username", 44).entityId().uniqueIndex()
    val salt = binary("salt", 16)
    val hash = binary("hash", 32)
}