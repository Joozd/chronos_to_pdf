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
    }



    private fun buildAircraftMap(): Map<String, Aircraft>{
        val aircraftList: List<Aircraft> = R.fromJsonFile("aircraft.json")!! // forced not null, exception is what we want if aircraft data is missing

        return aircraftList.associateBy { it.shortName }
    }

    private fun buildRegistrationMap(): Map<String, String>{
        val registrationsList: List<Registration> = R.fromJsonFile("registration.json")!! // forced not null, exception is what we want if aircraft data is missing

        return registrationsList.associate { it.registration.standardize() to it.type }
    }
}
