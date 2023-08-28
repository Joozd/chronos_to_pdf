package data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Create with [Config.getInstance]
 * use as config.getInstance()["password"]
 */
class Config(configLines: List<String>) {
    private val values = configLines.mapNotNull { nameToContentFromLine(it) }.toMap()

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

    companion object{
        private var _instance: Config? = null
        private val mutex = Mutex()
        suspend fun getInstance(): Config = mutex.withLock{
            _instance
                ?: Config(readConfigFileToLines() ?: error ("ERROR READING CONFIG FILE")).also{
                    _instance = it
                }
        }

        private fun readConfigFileToLines() =
            Config::class.java.classLoader.getResourceAsStream("/.conf")?.bufferedReader()?.readLines()
    }
}