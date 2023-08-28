package httphandlers

import data.LoginDataRepository
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
            val newLoginData = LoginDataRepository.generateUser()
            TODO("Send email with login data")
            redirect("/confirmation.html")
            javaClass.getResourceAsStream("/.conf")?.bufferedReader()?.useLines { lines -> // get email password. This does not go here of course but in email repository
                lines.forEach { println(it) }
            }
        }
    }
}