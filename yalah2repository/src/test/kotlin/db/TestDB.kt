package db

import org.jetbrains.exposed.sql.Database
import repository.database.DBConnector

/**
 * Can be injected into Repository
 */
object TestDB: DBConnector {
    private val _initialized by lazy {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }

    /**
     * Connect to database. This can be called as many times as you want and keeps the same connection.
     */
    override fun connect(){
        _initialized
    }
}