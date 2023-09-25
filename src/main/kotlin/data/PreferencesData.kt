package data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Preferences for postprocessing flights
 */
data class PreferencesData @JsonCreator constructor(
    @JsonProperty("logLanding") val logLanding: Boolean,
    @JsonProperty("guessSimType") val guessSimType: Boolean,
    @JsonProperty("removeSimTypes") val removeSimTypes: Boolean,
    @JsonProperty("function") val function: String,
){
    companion object{
        val DEFAULT get() = PreferencesData(logLanding = true, guessSimType = true, function = FO, removeSimTypes = false)
        // Function names
        const val CAPTAIN = "Captain"
        const val FO = "FO"
        const val SO = "SO"
    }
}
