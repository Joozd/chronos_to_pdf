import data.PreferencesData
import nl.joozd.joozdlogcommon.AugmentedCrew
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.pdflogbookbuilder.extensions.totalTime
import org.junit.jupiter.api.Test
import utils.extensions.distinctTimes
import utils.extensions.prepareForLogbook
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

class ListBasicFlightsExtTests {
    @Test
    fun testDistinctTimes(){
        val listWithDouble = listOf(picFlight, soFlight, picFlight_copy)
        assertEquals(2, listWithDouble.distinctTimes().size)
    }

    @Test
    fun testPrepareForLogbook(){
        val noMultiCrewPref = PreferencesData.DEFAULT.copy(multiCrewTimes = false) // default has this on
        val flightTime = 3000/60 // all test flights are 3000 seconds

        // check Pic gets full flights always
        assertEquals(flightTime, listOf(picFlight).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())
        assertEquals(picFlight.totalTime(), listOf(picFlight).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        assertEquals(14*60, listOf(picFlight_14hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        // check SO gets 2/3 on short flights
        assertEquals(flightTime*2/3, listOf(soFlight).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())
        assertEquals(flightTime*2/3, listOf(soFlight).prepareForLogbook(noMultiCrewPref).first().totalTime())

        // check SO gets 1/2 on long flights with multiCrew time on
        assertEquals(14*60/2, listOf(soFlight_14hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        // check SO gets 2/3 on long flights with multiCrew time on
        assertEquals(14*60*2/3, listOf(soFlight_14hr).prepareForLogbook(noMultiCrewPref).first().totalTime())

        // check FO gets all on short flight:
        assertEquals(flightTime, listOf(foFlight).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())
        assertEquals(picFlight.totalTime(), listOf(foFlight).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        // check FO gets 2/3 on medium flight:
        assertEquals(10*60*2/3, listOf(foFlight_10hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        // check FO gets all on medium flight when deselected:
        assertEquals(10*60, listOf(foFlight_10hr).prepareForLogbook(noMultiCrewPref).first().totalTime())

        // check FO gets 1/2 on long flight:
        assertEquals(14*60/2, listOf(foFlight_14hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())

        // check FO gets all on long flight when deselected:
        assertEquals(14*60, listOf(foFlight_14hr).prepareForLogbook(noMultiCrewPref).first().totalTime())

        // check undefined is treated as FO:
        assertEquals(14*60/2, listOf(undefinedFlight_14hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())
        assertEquals(10*60*2/3, listOf(undefinedFlight_10hr).prepareForLogbook(PreferencesData.DEFAULT).first().totalTime())
    }




    private val startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond // today, at start of day

    private val picFlight = BasicFlight.PROTOTYPE.copy(orig = "EHAM", dest = "EBBR", timeOut = startOfDay, timeIn = startOfDay + 3000, augmentedCrew = AugmentedCrew(size = 3).toInt(), isPIC = true)
    private val picFlight_copy = picFlight.copy()
    private val foFlight = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay + 6000, timeIn = startOfDay + 9000, augmentedCrew = AugmentedCrew().toInt())
    private val soFlight = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay + 6000, timeIn = startOfDay + 9000, augmentedCrew = AugmentedCrew.coco(0).toInt())


    private val foFlight_10hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 10*60*60, augmentedCrew = AugmentedCrew().toInt())
    private val foFlight_14hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 14*60*60, augmentedCrew = AugmentedCrew().toInt())

    private val picFlight_14hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 14*60*60, augmentedCrew = AugmentedCrew(size = 4).toInt(), isPIC = true)

    private val soFlight_14hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 14*60*60, augmentedCrew = AugmentedCrew(size = 3).toInt())

    private val undefinedFlight_10hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 10*60*60, augmentedCrew = AugmentedCrew().toInt())
    private val undefinedFlight_14hr = BasicFlight.PROTOTYPE.copy(orig = "EBBR", dest = "EHAM", timeOut = startOfDay, timeIn = startOfDay + 14*60*60, augmentedCrew = AugmentedCrew().toInt())


}