package httphandlers

import data.SessionData
import io.javalin.http.Context
import kotlinx.coroutines.launch
import nl.joozd.joozdlogcommon.BasicFlight
import nl.joozd.pdflogbookbuilder.PdfLogbookBuilder
import parsing.MockParser
import utils.extensions.defaultScope
import java.io.ByteArrayOutputStream
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

class UploadHandler: SessionHandler() {
    override fun handleWithSessionData(ctx: Context, sessionData: SessionData) {
        with(ctx) {
            if (!loggedIn(sessionData)) {
                redirect("/bad_login_data.html")
                return
            }

            val uploadedFiles = uploadedFiles()

            val scope = defaultScope
            if(scope == null)
                ctx.status(500)
            else{
                scope.launch {
                    MockParser().parse(uploadedFiles.map { it.filename() }) // this takes some time

                    val outputStream = ByteArrayOutputStream()
                    PdfLogbookBuilder(generateFlights(15)).buildToOutputStream(outputStream)
                    sessionData.downloadableFile = outputStream.toByteArray()
                    sessionData.downloadReady = true
                }
            }

            ctx.redirect("/wait.html")
        }
    }

    //TODO temp function can be removed when done
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
            val name = listOf("Henk de Vries", "SELF", "Boudewijn markies van Voorst tot Voorst", "H. Dinges").random()

            val amountOfLandings = ThreadLocalRandom.current().nextInt().absoluteValue % 2
            val takeOff = if (ThreadLocalRandom.current().nextBoolean()) DayNight.DAY else DayNight.NIGHT
            val landing = if (ThreadLocalRandom.current().nextBoolean()) DayNight.DAY else DayNight.NIGHT

            val flightNumber = "KL" + ThreadLocalRandom.current().nextInt(100, 999).toString()

            // Create the BasicFlight
            val flight = BasicFlight.PROTOTYPE.copy(
                orig = orig,
                dest = dest,
                timeOut = timeOut,
                timeIn = timeIn,
                aircraft = aircraft,
                registration = registration,
                takeOffDay = if (takeOff == DayNight.DAY) amountOfLandings else 0,
                takeOffNight = if (takeOff == DayNight.NIGHT) amountOfLandings else 0,
                landingDay = if (landing == DayNight.DAY) amountOfLandings else 0,
                landingNight = if (landing == DayNight.NIGHT) amountOfLandings else 0,
                flightNumber = flightNumber,
                name = name
            )

            flights.add(flight)
        }

        return flights
    }

    private enum class DayNight{
        DAY,
        NIGHT
    }
}