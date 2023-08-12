package utils

import global.Values
import io.javalin.http.Context
import java.lang.StringBuilder
import kotlin.random.Random

fun Context.badData() {
    status(400)
}

/**
 * Assigns a new sessionID to this session.
 * @param knownIDs a list of IDs that are already taken, so we can check for duplicates.
 * @return the generated ID
 */
fun Context.generateSessionID(knownIDs: Set<String> = emptySet()): String{
    var generatedID: String
    do { generatedID = generateRandomString() } while(generatedID in knownIDs)

    sessionAttribute(Values.SESSION_ID, generatedID)
    return generatedID
}

private fun generateRandomString(length: Int = 16): String{
    val sb = StringBuilder()
    while(sb.length < length){
        val nextChar = LETTERS_AND_NUMBERS[Random.nextInt(LETTERS_AND_NUMBERS.size)]
        sb.append(nextChar)
    }
    return sb.toString()
}

private val LETTERS_AND_NUMBERS: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')