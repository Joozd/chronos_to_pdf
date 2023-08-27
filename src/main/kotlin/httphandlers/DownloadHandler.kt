package httphandlers

import global.StatusKeeper
import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler
import nl.joozd.joozdlogcommon.BasicFlight
import java.io.InputStream

/**
 * This is a handler that provides the built PDF that should be available.
 */
class DownloadHandler: Handler {
    override fun handle(ctx: Context) {
        with (ctx) {
            val isDone = sessionAttribute<StatusKeeper>(Values.STATUS_KEEPER)?.downloadReady ?: false
            if (!isDone){
                status(400).result("Bad request, session not ready")
                return
            }
            val fileData = makeFile(emptyList())
            result(fileData)
                .contentType("text/plain") // "application/pdf"
                .header("Content-Disposition", "attachment; filename=TEST_FILE.txt")
            TODO("Above is just for reference. Build this from scratch.")
        }
    }

    private fun makeFile(data: List<BasicFlight>): InputStream = data.joinToString { it.toString() }.toByteArray().inputStream()
}