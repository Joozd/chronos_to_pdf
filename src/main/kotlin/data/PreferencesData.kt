package data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Preferences for postprocessing flights
 */
data class PreferencesData @JsonCreator constructor(
    @JsonProperty("logLanding") val logLanding: Boolean,
    @JsonProperty("guessSimType") val guessSimType: Boolean,
    @JsonProperty("removeSimTypes") val removeSimTypes: Boolean,
    @JsonProperty("defaultFunction") val defaultFunction: String,
){
    fun toJson() = jacksonObjectMapper().writeValueAsString(this)
    companion object{
        val DEFAULT get() = PreferencesData(logLanding = true, guessSimType = true, removeSimTypes = false, defaultFunction = FO)
        // Function names
        const val CAPTAIN = "Captain"
        const val FO = "FO"
        const val SO = "SO"
    }
}
