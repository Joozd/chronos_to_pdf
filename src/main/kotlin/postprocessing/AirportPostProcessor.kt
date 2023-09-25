package postprocessing

import data.PreferencesData
import data.R
import nl.joozd.joozdlogcommon.BasicFlight
import utils.TwilightCalculator
import kotlin.random.Random

/**
 * This takes care of airport data related postprocessing like iata -> icao and night hours.
 * Landing also are here because of day or night.
 */
object AirportPostProcessor: PostProcessor() {
    private val iataMap: Map<String, Airport>
    private val icaoMap: Map<String, Airport>
    init{
        val (iata, icao) = buildIataAndIcaoMap()
        iataMap = iata
        icaoMap = icao
    }

    override fun postProcess(flights: List<BasicFlight>, preferencesData: PreferencesData): List<BasicFlight> = flights.map{ f ->
        if (f.isSim) return@map f // do not postProcess sim sessions here
        val orig = icaoMap[f.orig] ?: iataMap[f.orig]  // replace orig with ICAO code
        val dest = icaoMap[f.dest] ?: iataMap[f.dest] // replace orig with ICAO code

        // no postprocessing if airport cannot be determined.
        if (orig == null || dest == null) return@map f

        val twilightCalculator = TwilightCalculator(f.timeOut)
        val nightTime = twilightCalculator.minutesOfNight(orig, dest, f.timeOut, f.timeIn)
        val landingDay: Int
        val landingNight: Int
        if(preferencesData.logLanding){
            // add 0 or 1 landings to this flight. Day and night are correct for time and place.
            val dayLanding = twilightCalculator.itIsDayAt(dest, f.timeIn)
            val landings = Random.nextInt(2)
            landingDay = if (dayLanding) landings else 0
            landingNight = landings - landingDay
        }

        else{
            landingDay = 0
            landingNight = 0
        }

        f.copy(
            orig = orig.ident,
            dest = dest.ident,
            nightTime = nightTime,
            landingDay = landingDay,
            landingNight = landingNight
        )
    }


    /**
     * Build two maps with Airport data: One with IATA codes as keys, and one with ICAO codes as keys.
     */
    private fun buildIataAndIcaoMap(): Pair<Map<String, Airport>, Map<String, Airport>> {
        val airportsList: List<Airport> = R.fromJson("airports.json")

        return airportsList.associateBy { it.iata_code.standardize() } to airportsList.associateBy { it.ident }
    }
}