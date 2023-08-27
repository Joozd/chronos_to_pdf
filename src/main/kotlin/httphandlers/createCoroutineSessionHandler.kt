package httphandlers

import global.Values
import jakarta.servlet.http.HttpSessionEvent
import jakarta.servlet.http.HttpSessionListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.eclipse.jetty.server.session.SessionHandler

fun createCoroutineSessionHandler(): SessionHandler = SessionHandler().apply{
    addEventListener(object : HttpSessionListener {
        override fun sessionCreated(se: HttpSessionEvent?) {
            val session = se?.session
            session?.setAttribute(Values.COROUTINE_JOB, SupervisorJob())
        }

        // Clean up stuff
        override fun sessionDestroyed(se: HttpSessionEvent?) {
            se?.session?.let{ session ->
                (session.getAttribute(Values.COROUTINE_JOB) as Job?)?.cancel()
                // remove session data?
            }
        }
    })
}