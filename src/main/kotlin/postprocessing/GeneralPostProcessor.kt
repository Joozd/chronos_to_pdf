package postprocessing

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.pdflogbookbuilder.extensions.totalTime
import java.util.*

/**
 * This takes care of postprocessing items that are not tied to aircraft or airport data.
 */
object GeneralPostProcessor: PostProcessor() {
    override fun postProcess(flights: List<BasicFlight>, preferencesData: PreferencesData): List<BasicFlight> = flights.map { f ->
        val name = if (f.isPIC) "SELF" else ""
        // TODO Maybe do something with guessing 3 and 4 person ops?

        f.copy(
            ifrTime = f.totalTime(),
            name = name
        ) // all imported flights are IFR.
    }.postProcessSim(preferencesData)


    /**
     * This guesses the type of sim based on what aircraft has been flown on the first flight after this simulator duty.
     * Does not change type if it has been entered.
     * THIS NEEDS A SORTED LIST
     */
    private fun List<BasicFlight>.postProcessSim(preferencesData: PreferencesData): List<BasicFlight> { // = mapIndexed{ i, f ->
        if (!preferencesData.addTypeToSim) return this

        val currentList = LinkedList(this)

        var currentTime = Long.MIN_VALUE

        return buildList {
            while (currentList.isNotEmpty()) {
                val f = currentList.removeFirst()

                // If list is unsorted, this will fail.
                if (f.timeOut < currentTime){
                    logger.error("Trying to get aircraft type with an unsorted list")
                    return this
                }
                currentTime = f.timeOut

                if (f.isSim && f.aircraft.isBlank()) {
                    val type = currentList.firstOrNull { it.aircraft.isNotBlank() }?.aircraft ?: ""
                    add(f.copy(aircraft = type))
                }
                else
                    add(f)
            }
        }.toList()
    }
}