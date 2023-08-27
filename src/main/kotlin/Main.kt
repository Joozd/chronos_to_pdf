import httphandlers.*
import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database

fun main() {
    // connect Database
    Database.connect("jdbc:h2:./database", driver = "org.h2.Driver")

    Javalin.create { config ->
        config.staticFiles.add("/public")
        config.jetty.sessionHandler{ createCoroutineSessionHandler() }
    }
        .get("/", MainHandler())
        .get("/status", StatusHandler())
        .get("/download", DownloadHandler())
        .post("/send_email", EmailHandler())
        .post("/upload", UploadHandler())
        .start(7070)
}