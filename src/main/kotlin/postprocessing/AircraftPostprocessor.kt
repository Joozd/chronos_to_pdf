package postprocessing

import data.PreferencesData
import data.R
import nl.joozd.joozdlogcommon.BasicFlight

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
     */
    private fun List<BasicFlight>.postProcessSim(preferencesData: PreferencesData): List<BasicFlight> = mapIndexed{ i, f ->
        if (!preferencesData.addTypeToSim || !f.isSim || f.aircraft.isNotBlank()) return@mapIndexed f // only change sim flights that have no type data yet

        // if no aircraft flown after this duty, don't add a type. I thought about the last one flown before,
        // but that would give wrong type on a TQ, which probably spans the last entry in a month.
        // This way, it will get corrected when the first roster with a flight in it is uploaded.
        val type = this.drop(i+1).firstOrNull { it.aircraft.isNotBlank() }?.aircraft ?: "" // first aircraft type flown after this sim duty.
        f.copy(aircraft = type)
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
