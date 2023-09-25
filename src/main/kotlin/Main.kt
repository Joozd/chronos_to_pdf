import data.flightsdata.EncryptedUserDataTable
import data.logindata.Users
import httphandlers.*
import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    // connect Database
    Database.connect("jdbc:h2:./database", driver = "org.h2.Driver")
    transaction {
        if(!Users.exists())
            SchemaUtils.create(Users)

        if(!EncryptedUserDataTable.exists())
            SchemaUtils.create(EncryptedUserDataTable)

    }

    Javalin.create { config ->
        config.staticFiles.add("/public")
        config.jetty.sessionHandler{ createCoroutineSessionHandler() }
    }
        .get("/", MainHandler())
        .get("/status", StatusHandler())
        .get("/download", DownloadHandler())
        .get("/create_new_account", CreateNewAccountHandler())

        .post("check_existing", CheckIfEmailExistsHandler())
        .post("/upload", UploadHandler())
        .post("/update_preferences", UpdatePreferencesHandler())
        .start(7070)
}