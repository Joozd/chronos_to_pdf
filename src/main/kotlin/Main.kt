import global.Values
import handlers.PostHandler
import io.javalin.Javalin
import utils.TemporaryResultObject
import utils.badData
import utils.generateSessionID
import java.nio.file.Files
import java.nio.file.Paths

private val temporaryResults = HashMap<String, TemporaryResultObject>()
fun main() {
    Javalin.create(/*config*/)
        .get("/chronos") { ctx ->
            val path = Paths.get("html/index.html")
            ctx.generateSessionID(temporaryResults.keys)

            // Ensure the file exists
            if (Files.exists(path)) {
                ctx.contentType("text/html")
                ctx.result(Files.readString(path))
            } else {
                ctx.status(404).result("File not found!")
            }
        }
        .get("/result/") { ctx ->
            val sessionID = ctx.sessionAttribute<String>(Values.SESSION_ID) ?: return@get ctx.badData().also { println("BAD BAD DATA :(")}
            ctx.result("Result: " + (temporaryResults[sessionID]?.result ?: "Bad Session ID $sessionID"))
        }
        .post("/chronos/", PostHandler(temporaryResults))
        .start(7070)
}