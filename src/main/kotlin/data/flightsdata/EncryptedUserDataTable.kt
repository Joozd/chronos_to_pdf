package data.flightsdata

import org.jetbrains.exposed.dao.id.IdTable

object EncryptedUserDataTable : IdTable<String>() {
    override val id = char("username", 16).entityId().uniqueIndex() // Username as primary key
    val lastAccessed = long("last_accessed") // Last accessed, epoch second
    val encryptedData = blob("encrypted_data") // BLOB column for encrypted data
}