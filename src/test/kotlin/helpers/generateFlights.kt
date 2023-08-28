package helpers

import nl.joozd.joozdlogcommon.BasicFlight
import java.util.concurrent.ThreadLocalRandom
import helpers.DayNight.*


fun generateFlights(numFlights: Int): List<BasicFlight> {
    val flights = mutableListOf<BasicFlight>()

    for (i in 1..numFlights) {
        // Generate other random attributes
        val orig = listOf("EHAM", "EBBR", "KJFK", "OMDB").random()
        val dest = listOf("EHAM", "EBBR", "KJFK", "OMDB").random()
        val timeOut = System.currentTimeMillis() / 1000 - ThreadLocalRandom.current().nextInt(1*86400, 3000 * 86400) // timeOut is between 3000 and 1 days ago
        val timeIn = timeOut + ThreadLocalRandom.current().nextInt(900, 36000) // 15 to 600 minutes

        val aircraft = listOf("B77W", "B772", "B789", "B78X").random()
        val registration = "PH-B" + ('A'..'Z').random().toString() + ('A'..'Z').random().toString()

        val amountOfLandings = ThreadLocalRandom.current().nextInt() % 2
        val takeOff = if (ThreadLocalRandom.current().nextBoolean()) DAY else NIGHT
        val landing = if (ThreadLocalRandom.current().nextBoolean()) DAY else NIGHT

        val flightNumber = "KL" + ThreadLocalRandom.current().nextInt(100, 999).toString()

        // Create the BasicFlight
        val flight = BasicFlight.PROTOTYPE.copy(
            orig = orig,
            dest = dest,
            timeOut = timeOut,
            timeIn = timeIn,
            aircraft = aircraft,
            registration = registration,
            takeOffDay = if (takeOff == DAY) amountOfLandings else 0,
            takeOffNight = if (takeOff == NIGHT) amountOfLandings else 0,
            landingDay = if (landing == DAY) amountOfLandings else 0,
            landingNight = if (landing == NIGHT) amountOfLandings else 0,
            flightNumber = flightNumber
        )

        flights.add(flight)
    }

    return flights
}

private enum class DayNight{
    DAY,
    NIGHT
}