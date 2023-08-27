package httphandlers

import global.StatusKeeper
import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler
import kotlinx.coroutines.launch
import parsing.MockParser
import utils.TemporaryResultObject
import utils.extensions.defaultScope

class UploadHandler: Handler {
    override fun handle(ctx: Context) {
        with(ctx) {
            val uploadedFiles = uploadedFiles()
            val fileNames = uploadedFiles.joinToString { it.filename() }

            // Session might not be available when the parser is done, but this object will be.
            val statusKeeper = sessionAttribute(Values.STATUS_KEEPER) ?: StatusKeeper().also{ sessionAttribute(Values.STATUS_KEEPER, it)}

            sessionAttribute(Values.TEMPORARY_RESULT, TemporaryResultObject().apply {
                result = fileNames
            })

            val scope = defaultScope
            if(scope == null)
                ctx.status(500)
            else{
                scope.launch {
                    MockParser().parse(uploadedFiles.map { it.filename() })
                    statusKeeper.downloadReady = true
                }
            }

            ctx.redirect("/wait.html")
        }
    }
}