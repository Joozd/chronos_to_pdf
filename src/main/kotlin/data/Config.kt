package data

/**
 * use as Config["password"]
 */
class Config private constructor (lines: List<String>?) {
    init{
        println("TEST: $lines")
    }
    private val values = lines?.mapNotNull { nameToContentFromLine(it) }?.toMap()
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

    companion object{
        private val _instance by lazy {
            Config(R.textFile(".conf")?.lines())
        }
        fun getInstance() = _instance
        fun getMockInstance(mockedFileContents: List<String>?) = Config(mockedFileContents)
    }


}