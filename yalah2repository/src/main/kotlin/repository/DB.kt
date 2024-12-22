package repository

import org.jetbrains.exposed.sql.Database
import repository.database.DBConnector

internal object DB: DBConnector {
    private val _initialized by lazy { Database.connect("jdbc:h2:./yaladatabase", driver = "org.h2.Driver") }

    /**
     * Connect to database. This can be called as many times as you want and keeps the same connection.
     */
    override fun connect(){
        _initialized
    }
}