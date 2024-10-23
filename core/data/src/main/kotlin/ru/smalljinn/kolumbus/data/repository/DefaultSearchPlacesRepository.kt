package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.smalljinn.database.dao.SearchPlaceDao
import ru.smalljinn.database.model.asModels
import ru.smalljinn.model.data.Place
import javax.inject.Inject

class DefaultSearchPlacesRepository @Inject constructor(
    private val searchPlaceDao: SearchPlaceDao
): SearchPlacesRepository {
    override fun searchPlaces(query: String): Flow<List<Place>> {
        return searchPlaceDao.searchPlaces(query).map { it.asModels() }
    }

    override fun getCount(): Flow<Int> {
        return searchPlaceDao.getPlacesCount()
    }
}