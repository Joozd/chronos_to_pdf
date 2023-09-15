import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import postprocessing.Aircraft
import postprocessing.Airport
import postprocessing.Registration
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This is
 */
fun main() {
    val aircraftPath = Paths.get("c:\\joozdlog\\aircrafttypes_7.txt")
    val registrationPath = Paths.get("c:\\joozdlog\\forcedtypes_5.txt")
    val aircraftLines = Files.readAllLines(aircraftPath)
    val registrationLines = Files.readAllLines(registrationPath)

    val aircraftData = aircraftLines
        .filter{ it.startsWith('\"')}
        .map{lineToData(it)}

    val registrationData = registrationLines
        .filter{ it.startsWith('\"')}
        .map{lineToData(it)}


    val aircraftList = aircraftData.map { Aircraft(it) }
    val registrationsList = registrationData.map { Registration(it) }

    val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )
    val aircraftJsonString = objectMapper.writeValueAsString(aircraftList)
    val registrationJsonString = objectMapper.writeValueAsString(registrationsList)

    Files.writeString(Paths.get("c:\\joozdlog\\aircraft.json"), aircraftJsonString)
    Files.writeString(Paths.get("c:\\joozdlog\\registration.json"), registrationJsonString)

    val airportFile = File("c:\\joozdlog\\airports_2.csv")

    val csvMapper = CsvMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val csvSchema: CsvSchema = CsvSchema.emptySchema().withHeader()

    val it: MappingIterator<Airport> = csvMapper
        .readerFor(Airport::class.java)
        .with(csvSchema)
        .readValues(airportFile)

    val airports: List<Airport> = it.readAll()

    val airportJsonString = objectMapper.writeValueAsString(airports)

    Files.writeString(Paths.get("c:\\joozdlog\\airports.json"), airportJsonString)
}

private fun lineToData(line: String) =
    line.drop(1).dropLast(1)
        .split("\",\"")