package utils.extensions

import nl.joozd.joozdlogcommon.BasicFlight
import postprocessing.AircraftPostprocessor
import postprocessing.AirportPostProcessor
import postprocessing.GeneralPostProcessor

fun List<BasicFlight>.postProcess(): List<BasicFlight> {
    var list = AircraftPostprocessor.postProcess(this)
    list = GeneralPostProcessor.postProcess(list)
    list = AirportPostProcessor.postProcess(list)
    //And the same for any other postprocessing steps.
    return list
}

fun List<BasicFlight>.distinctTimes(): List<BasicFlight> =
    this.distinctBy { it.timeOut to it.timeIn }