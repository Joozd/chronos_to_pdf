package data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * use as Config["password"]
 */
object Config {
    private val values = R.textFile(".conf")?.lines()?.mapNotNull { nameToContentFromLine(it) }?.toMap()
        ?: emptyMap()

    operator fun get(key: String): String? = values[key]

    private fun nameToContentFromLine(line: String): Pair<String, String>? {
        if (line.isBlank()                                  // empty line
            || line.trim().firstOrNull() == '#'             // Line marked as comment with #
            || '=' !in line && line.count { it == '\"'} < 2 // invalid line
        ) return null
        val lengthOfName = line.indexOf('=')
        val name = line.take(lengthOfName)
        val firstQuote = line.indexOf('\"')
        val lastQuote = line.indexOfLast { it == '\"'}
        val content = line.substring((firstQuote + 1)..< lastQuote)

        return name to content
    }


}