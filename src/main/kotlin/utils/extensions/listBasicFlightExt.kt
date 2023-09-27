package utils.extensions

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import postprocessing.AircraftPostprocessor
import postprocessing.AirportPostProcessor
import postprocessing.GeneralPostProcessor

/**
 * THIS NEEDS A SORTED LIST or things will go wrong.
 */
fun List<BasicFlight>.postProcess(preferencesData: PreferencesData): List<BasicFlight> {
    var list = AircraftPostprocessor.postProcess(this, preferencesData)
    list = GeneralPostProcessor.postProcess(list, preferencesData)
    list = AirportPostProcessor.postProcess(list, preferencesData)
    //And the same for any other postprocessing steps.
    return list
}

fun List<BasicFlight>.distinctTimes(): List<BasicFlight> =
    this.distinctBy { it.timeOut to it.timeIn }

/**
 * Prepares flights for logbook by applying the non-permanent preferences in [preferencesData]
 */
fun List<BasicFlight>.prepareForLogbook(preferencesData: PreferencesData) = map { f ->
    f.prepareFlightForLogbook(preferencesData)
}

/**
 * Prepares flight for logbook by applying the non-permanent preferences in [preferencesData]
 * Current non-permanent options:
 * - multiCrewTimes
 */
private fun BasicFlight.prepareFlightForLogbook(preferencesData: PreferencesData) =
    setMultiCrewTimes(preferencesData)


private fun BasicFlight.setMultiCrewTimes(preferencesData: PreferencesData): BasicFlight{
    if(isSim) return this // sim times are not adjusted
    if(isPIC) return this // PIC times are not adjusted
    val outToInMinutes = (timeIn - timeOut).toInt() / 60 // in minutes

    // timeOfFlight of 0 means the Flight decides based on AugmentedCrew value
    val timeOfFlight: Int = when{
        !preferencesData.multiCrewTimes -> 0                    // If no preference, stick to what the flight gives us.
        outToInMinutes <= 9*60          -> 0                    // <9 hours: Stick to what the logged flight gives us
        outToInMinutes <= 12*60         -> (outToInMinutes*2/3) // 9-12 hours: log 2/3
        else                            -> outToInMinutes/2     // +12 hours: Log 1/2
    }

    // correctedTotalTime of 0 means the Flight decides based on AugmentedCrew value
    return this.copy(correctedTotalTime = timeOfFlight)
}
