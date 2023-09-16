package postprocessing

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight

abstract class PostProcessor {
    abstract fun postProcess(flights: List<BasicFlight>, preferencesData: PreferencesData): List<BasicFlight>

    /**
     * Uppercase and only letters
     */
    protected fun String.standardize() = uppercase().filter { c -> c in ('A'..'Z') }
}