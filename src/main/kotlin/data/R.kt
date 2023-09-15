package data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

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
        inline fun <reified T> fromJson(fileName: String): T{
            val objectMapper  = ObjectMapper().registerModule(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )

            val jsonString = textFile(fileName)

            val typeRef = object : TypeReference<T>() {}
            return objectMapper.readValue(jsonString, typeRef)
        }
    }
}