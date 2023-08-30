package data

class R {
    companion object {
        fun textFile(fileName: String): String? =
            Config::class.java.classLoader.getResourceAsStream("/$fileName")?.bufferedReader()?.readText()
    }
}