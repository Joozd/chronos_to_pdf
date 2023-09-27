package data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Preferences for postprocessing flights
 */
data class PreferencesData @JsonCreator constructor(
    @JsonProperty("logLanding") val logLanding: Boolean,            // add random landings to 50% of flights
    @JsonProperty("guessSimType") val guessSimType: Boolean,        // guess sim type based on first flight after
    @JsonProperty("removeSimTypes") val removeSimTypes: Boolean,    // remove all sim type data from all flights
    @JsonProperty("multiCrewTimes") val multiCrewTimes: Boolean,    // multi-crew time for non-pic: 9-11 hours = 2/3, 12+hrs = 1/2
    @JsonProperty("defaultFunction") val defaultFunction: String,   // Default function to be used if not found in import
){
    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)
    companion object{
        val DEFAULT get() = PreferencesData(logLanding = true, guessSimType = true, removeSimTypes = false, multiCrewTimes = true, defaultFunction = FO)

        // Function names
        const val CAPTAIN = "Captain"
        const val FO = "FO"
        const val SO = "SO"
    }
}
