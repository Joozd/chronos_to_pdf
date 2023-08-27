package utils.extensions

import global.Values
import io.javalin.http.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

fun Context.sessionID(): String = req().session.id

private val Context.coroutineContext get() = sessionAttribute<Job?>(Values.COROUTINE_JOB)

/**
 * Use this for IO operations
 */
val Context.ioScope get() = coroutineContext?.let { CoroutineScope(Dispatchers.IO + it) }

/**
 * Use this for CPU intensive ops
 */
val Context.defaultScope get() = coroutineContext?.let { CoroutineScope(Dispatchers.Default + it) }