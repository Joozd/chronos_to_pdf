package postprocessing

import data.PreferencesData
import data.R
import nl.joozd.joozdlogcommon.BasicFlight
import java.util.*

/**
 * This takes care of aircraft types and if they are multi pilot or not.
 */
object AircraftPostprocessor: PostProcessor() {
    private val aircraftMap = buildAircraftMap() // map of shortName to Aircraft
    private val registrationMap = buildRegistrationMap() // map of registration to shortname

    override fun postProcess(flights: List<BasicFlight>, preferencesData: PreferencesData): List<BasicFlight> = flights.map{ f ->
        if(f.isSim) return@map f // do not process sim outside postProcessSim
        val aircraftType = registrationMap[f.registration.standardize()] ?: f.aircraft
        val aircraft = aircraftMap[aircraftType]
            ?: return@map f // get aircraft. If no type found, no postprocessing is done for this flight.

        val isCopilot = aircraft.multiPilot && !f.isPIC
        val multiPilotTime = if (aircraft.multiPilot) (f.timeIn - f.timeOut).toInt() / 60 else 0


        f.copy(
            aircraft = aircraftType,
            isCoPilot = isCopilot,
            multiPilotTime = multiPilotTime,
            )
    }.postProcessSim(preferencesData)

    /**
     * This guesses the type of sim based on what aircraft has been flown on the first flight after this simulator duty.
     * Does not change type if it has been entered.
     * THIS NEEDS A SORTED LIST
     */
    private fun List<BasicFlight>.postProcessSim(preferencesData: PreferencesData): List<BasicFlight> { // = mapIndexed{ i, f ->
        if (!preferencesData.addTypeToSim) return this

        val currentList = LinkedList(this)

        var currentTime = Long.MIN_VALUE

        return buildList {
            while (currentList.isNotEmpty()) {
                val f = currentList.removeFirst()

                // If list is unsorted, this will fail.
                if (f.timeOut < currentTime){
                    logger.error("Trying to get aircraft type with an unsorted list")
                    return this
                }
                currentTime = f.timeOut

                if (!f.isSim || f.aircraft.isNotBlank()) {
                    val type = currentList.firstOrNull { it.aircraft.isNotBlank() }?.aircraft ?: ""
                    add(f.copy(aircraft = type))
                }
                else
                    add(f)
            }
        }.toList()
    }

    private fun buildAircraftMap(): Map<String, Aircraft>{
        val aircraftList: List<Aircraft> = R.fromJson("aircraft.json")

        return aircraftList.associateBy { it.shortName }
    }

    private fun buildRegistrationMap(): Map<String, String>{
        val registrationsList: List<Registration> = R.fromJson("registration.json")

        return registrationsList.associate { it.registration.standardize() to it.type }
    }
}
