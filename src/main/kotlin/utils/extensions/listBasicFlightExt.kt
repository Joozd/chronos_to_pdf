package utils.extensions

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import postprocessing.AircraftPostprocessor
import postprocessing.AirportPostProcessor
import postprocessing.GeneralPostProcessor

fun List<BasicFlight>.postProcess(preferencesData: PreferencesData): List<BasicFlight> {
    var list = AircraftPostprocessor.postProcess(this, preferencesData)
    list = GeneralPostProcessor.postProcess(list, preferencesData)
    list = AirportPostProcessor.postProcess(list, preferencesData)
    //And the same for any other postprocessing steps.
    return list
}

fun List<BasicFlight>.distinctTimes(): List<BasicFlight> =
    this.distinctBy { it.timeOut to it.timeIn }