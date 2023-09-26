package data.userprefs

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EncryptedUserPrefs(id: EntityID<String>): Entity<String>(id) {
    companion object: EntityClass<String, EncryptedUserPrefs>(EncryptedUserPrefsTable)
    var lastAccessed by EncryptedUserPrefsTable.lastAccessed
    var encryptedData by EncryptedUserPrefsTable.encryptedData
}