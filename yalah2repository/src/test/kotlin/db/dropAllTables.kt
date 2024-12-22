package db

import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Drop all tables from connected database.
 * For testing purposes, as H2 in-memory database is not cleared after each test.
 * Use:
 * @AfterEach
 * fun cleanup() {
 *     transaction {
 *         SchemaUtils.drop(UsersTable, OrdersTable, ProductsTable /* Add other tables here */)
 *     }
 * }
 */
internal fun dropAllTables() {
    transaction {
        val tableNames = exec("""
            SELECT TABLE_NAME 
            FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_SCHEMA = 'PUBLIC'
        """) { rs ->
            generateSequence {
                if (rs.next()) rs.getString("TABLE_NAME") else null
            }.toList()
        }

        tableNames?.forEach { tableName ->
            exec("DROP TABLE IF EXISTS $tableName CASCADE")
        }
    }
}
