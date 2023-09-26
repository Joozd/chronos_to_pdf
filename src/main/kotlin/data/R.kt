package data

import utils.jsonToObject

class R {
    companion object {
        /**
         * use R.textFile("my_file.txt")
         */
        fun textFile(fileName: String): String? =
            Config::class.java.classLoader.getResourceAsStream(fileName)?.bufferedReader()?.readText()

        /**
         * Get a list of json-serialized objects.
         * Only works on lists of objects.
         */
        inline fun <reified T> fromJsonFile(fileName: String): T?{
            val jsonString = textFile(fileName) ?: return null
            return jsonToObject(jsonString)
        }
    }
}