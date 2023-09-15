package postprocessing

import nl.joozd.joozdlogcommon.BasicFlight

abstract class PostProcessor {
    abstract fun postProcess(flights: List<BasicFlight>): List<BasicFlight>

    /**
     * Uppercase and only letters
     */
    protected fun String.standardize() = uppercase().filter { c -> c in ('A'..'Z') }
}