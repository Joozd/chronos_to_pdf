import data.PreferencesData
import nl.joozd.joozdlogcommon.BasicFlight
import postprocessing.GeneralPostProcessor
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class PostProcessingTest {
    @Test
    fun testGeneralPostProcess(){
        val testDay = LocalDate.ofYearDay(2000, 1).atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
        val simFlight = BasicFlight.PROTOTYPE.copy(timeOut = testDay, isSim = true, simTime = 210)
        val testType = "TEST"


        val nextFlight = BasicFlight.PROTOTYPE.copy(aircraft = testType, timeOut = testDay + 100000, timeIn = testDay + 110000) // normal flight, a bit over a day later, duration = 10000
        val picFlight = BasicFlight.PROTOTYPE.copy(aircraft = "XXX1", timeOut = testDay + 200000, timeIn = testDay + 210000, isPIC = true) // normal flight, a bit over a day later, duration = 10000
        val notPicFlight = BasicFlight.PROTOTYPE.copy(aircraft = "XXX2", timeOut = testDay + 300000, timeIn = testDay + 310000) // normal flight, a bit over a day later, duration = 10000
        val flights = listOf(simFlight, nextFlight,picFlight, notPicFlight)
        val processedFlights = GeneralPostProcessor.postProcess(flights, PreferencesData(addLandings = false, addTypeToSim = true))

        //check if type gets added to sim
        assertEquals("", flights.first().aircraft)
        assertEquals(testType, processedFlights.first().aircraft)

        // check if IFR time gets added to flight
        assertEquals(0, flights[1].ifrTime)
        assertEquals(10000 / 60, processedFlights[1].ifrTime)

        //test if SELF gets added to PIC flight
        assertEquals("", flights[2].name)
        assertEquals("SELF", processedFlights[2].name)
        assertEquals("", processedFlights[3].name)
    }
}