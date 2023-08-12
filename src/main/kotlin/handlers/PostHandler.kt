package handlers

import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.TemporaryResultObject
import utils.badData

class PostHandler(val temporaryResultsMap: MutableMap<String, TemporaryResultObject>): Handler {
    override fun handle(ctx: Context) {
        println("BOTERHAM")
        val sessionID = ctx.sessionAttribute<String>(Values.SESSION_ID) ?: return ctx.badData()
        val uploadedFiles = ctx.uploadedFiles()
        val fileNames = uploadedFiles.joinToString { it.filename() }
        println(fileNames)
        temporaryResultsMap[sessionID] = TemporaryResultObject().apply{
            result = fileNames
        }
        ctx.redirect("/result/")
    }
}