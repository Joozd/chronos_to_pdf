package httphandlers

import global.Values
import io.javalin.http.Context
import io.javalin.http.Handler

/**
 * Receive a POST to email user
 */
class EmailHandler: Handler {
    override fun handle(ctx: Context) {
        with(ctx) {
            println("Email received: ${formParam(Values.EMAIL)}")
            redirect("/confirmation.html")
        }
    }
}