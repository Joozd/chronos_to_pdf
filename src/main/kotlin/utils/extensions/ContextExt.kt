package utils.extensions

import data.SessionData
import global.Values
import io.javalin.http.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Gets the sessionData object for this session. If none exists, it will be created.
 */
fun Context.getSessionData(): SessionData = synchronized(this) {
    sessionAttribute(Values.SESSION_DATA) ?: SessionData().also {
        sessionAttribute(Values.SESSION_DATA, it)
    }
}

private val Context.coroutineContext get() = sessionAttribute<Job?>(Values.COROUTINE_JOB)

/**
 * Use this for IO operations
 */
val Context.ioScope get() = coroutineContext?.let { CoroutineScope(Dispatchers.IO + it) }

/**
 * Use this for CPU intensive ops
 */
val Context.defaultScope get() = coroutineContext?.let { CoroutineScope(Dispatchers.Default + it) }