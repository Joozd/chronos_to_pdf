package postprocessing

import data.R
import nl.joozd.joozdlogcommon.BasicFlight
import utils.TwilightCalculator

/**
 * This takes care of airport data related postprocessing like iata -> icao and night hours.
 */
object AirportPostProcessor: PostProcessor() {
    private val iataMap: Map<String, Airport>
    private val icaoMap: Map<String, Airport>
    init{
        val (iata, icao) = buildIataAndIcaoMap()
        iataMap = iata
        icaoMap = icao
    }

    override fun postProcess(flights: List<BasicFlight>): List<BasicFlight> = flights.map{ f ->
        if (f.isSim) return@map f // do not postProcess sim sessions here
        val orig = icaoMap[f.orig] ?: iataMap[f.orig]  // replace orig with ICAO code
        val dest = icaoMap[f.dest] ?: iataMap[f.dest] // replace orig with ICAO code

        // no postprocessing if airport cannot be determined.
        if (orig == null || dest == null) return@map f

        val nightTime = TwilightCalculator(f.timeOut).minutesOfNight(orig, dest, f.timeOut, f.timeIn)

        f.copy(
            orig = orig.ident,
            dest = dest.ident,
            nightTime = nightTime
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