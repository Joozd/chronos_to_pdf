import data.LoginDataRepository
import data.flightsdata.EncryptedUserDataTable
import data.logindata.Users
import data.userprefs.EncryptedUserPrefsTable
import global.Values
import httphandlers.*
import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("MainKt")
    // connect Database
    Database.connect("jdbc:h2:./database", driver = "org.h2.Driver")
    createTables()
    println("BLABLA")

    LoginDataRepository.createNewUser("test.user@klm.com").also{loginWithKey ->
        logger.info("test user: ?${Values.UID}=${loginWithKey.uid}&${ Values.KEY}=${loginWithKey.base64Key}")
    }


    Javalin.create { config ->
        config.staticFiles.add("/public")
        config.jetty.sessionHandler{ createCoroutineSessionHandler() }
    }
        .get("/", MainHandler())
        .get("/status", StatusHandler())
        .get("/download", DownloadHandler())
        .get("/create_new_account", CreateNewAccountHandler())
        .get("/prefs", PrefsHandler())

        .post("check_existing", CheckIfEmailExistsHandler())
        .post("/upload", UploadHandler())
        .post("/update_preferences", UpdatePreferencesHandler())
        .start(7070)
}

private fun createTables() {
    transaction {
        if (!Users.exists())
            SchemaUtils.create(Users)

        if (!EncryptedUserDataTable.exists())
            SchemaUtils.create(EncryptedUserDataTable)

        if (!EncryptedUserPrefsTable.exists())
            SchemaUtils.create(EncryptedUserPrefsTable)
    }
}