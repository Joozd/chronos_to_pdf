package parsing

import nl.joozd.joozdlogcommon.BasicFlight

class MockParser: Parser{
    override fun parse(lines: List<String>): Collection<BasicFlight> {
        println("WE BE DOING SOOOOO MUCH WORK")
        repeat(5){
            Thread.sleep(1000)
            println(it)
        }
        println("Done!")
        return lines.map { BasicFlight.PROTOTYPE.copy (remarks = it)}
    }

}