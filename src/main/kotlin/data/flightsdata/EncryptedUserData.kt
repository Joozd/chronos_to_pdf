package data.flightsdata

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * NOTE: ID must be 16 characters long
 */
class EncryptedUserData(id: EntityID<String>): Entity<String>(id) {
    companion object: EntityClass<String, EncryptedUserData>(EncryptedUserDataTable)
    var lastAccessed by EncryptedUserDataTable.lastAccessed
    var encryptedData by EncryptedUserDataTable.encryptedData
}