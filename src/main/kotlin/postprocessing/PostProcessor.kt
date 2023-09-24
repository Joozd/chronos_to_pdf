package postprocessing

import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import org.slf4j.LoggerFactory

abstract class PostProcessor {
    protected val logger = LoggerFactory.getLogger(this::class.java)
    abstract fun postProcess(flights: List<BasicFlight>, preferencesData: PreferencesData): List<BasicFlight>

    /**
     * Uppercase and only letters
     */
    protected fun String.standardize() = uppercase().filter { c -> c in ('A'..'Z') }
}