import nl.joozd.yalainterfaces.interfaces.repository.*
import org.jetbrains.exposed.sql.transactions.transaction
import repository.DB
import repository.MetadataRepository
import repository.database.DBConnector
import utils.logger
import java.sql.Connection

class YalaH2Repository(dbConnector: DBConnector = DB): Repository {
    private val logger get() = logger()

    init{
        // Connect the database.
        dbConnector.connect()
        transaction {
            val metadata = (this.connection.connection as Connection).metaData

            logger.info("Database URL: ${metadata.url}")
            logger.info("Database User: ${metadata.userName}")
            logger.info("Database Product: ${metadata.databaseProductName}")
            logger.info("Database Version: ${metadata.databaseProductVersion}")
        }
    }

    override fun getAircraftTypesRepository(): AircraftTypesRepository {
        TODO("Not yet implemented")
    }

    override fun getAircraftRepository(): AircraftRepository {
        TODO("Not yet implemented")
    }

    override fun getAirportsRepository(): AirportsRepository {
        TODO("Not yet implemented")
    }

    override fun getCrewMembersRepository(): CrewMemberRepository {
        TODO("Not yet implemented")
    }

    override fun getDutyRepository(): DutyRepository {
        TODO("Not yet implemented")
    }

    override fun getPreferencesRepository(): PreferencesRepository {
        TODO("Not yet implemented")
    }

    internal fun getMetaDataRepository() = MetadataRepository()
}