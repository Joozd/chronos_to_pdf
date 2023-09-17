package postprocessing

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.pdflogbookbuilder.extensions.totalTime

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
    }
}