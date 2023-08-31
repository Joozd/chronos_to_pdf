package data.logindata

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<String>): Entity<String>(id) {
    fun toLoginData() = LoginData(id.value, salt, hash)
    companion object: EntityClass<String, User>(Users)
    var salt by Users.salt
    var hash by Users.hash
}