package parsing

import nl.joozd.joozdlogcommon.BasicFlight

interface Parser {
    fun parse(lines: List<String>): Collection<BasicFlight>
}