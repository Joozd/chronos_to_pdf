package repository

import nl.joozd.yalainterfaces.dto.AircraftTypeData
import nl.joozd.yalainterfaces.dto.RepositoryMetadata
import nl.joozd.yalainterfaces.interfaces.repository.AircraftTypesRepository
import nl.joozd.yalainterfaces.interfaces.repository.searchers.aircrafttypes.AircraftTypeSearchBuilder

class H2AircraftTypesRepository: AircraftTypesRepository() {
    override suspend fun updateData(): Result<Boolean> {
        return super.updateData()
    }

    override suspend fun getMetaData(): List<RepositoryMetadata> {
        return super.getMetaData()
    }

    override fun buildAircraftTypesQuery(): AircraftTypeSearchBuilder {
        TODO("Not yet implemented")
    }

    override fun getAllTypes(): kotlinx.coroutines.flow.Flow<List<AircraftTypeData>> {
        TODO("Not yet implemented")
    }
}