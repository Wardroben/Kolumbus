package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.dao.SearchPlaceDao
import ru.smalljinn.database.model.asModels
import ru.smalljinn.database.model.toPlaceFts
import ru.smalljinn.model.data.Place
import javax.inject.Inject

class DefaultSearchPlacesRepository @Inject constructor(
    private val searchPlaceDao: SearchPlaceDao,
    private val placeDao: PlaceDao
) : SearchPlacesRepository {

    override suspend fun populateFtsData() {
        withContext(Dispatchers.IO) {
            searchPlaceDao.insertAll(
                placeDao.getPlacesStream().first().map { it.toPlaceFts() }
            )
        }
    }

    override fun searchPlaces(query: String): Flow<List<Place>> {
        val placeStringIds: Flow<List<String>> = searchPlaceDao.searchPlaces("*$query*")
        val placesFlow = placeStringIds
            .mapLatest { stringIds ->
                stringIds
                    .map { stringId -> stringId.toLong() }
                    .toSet()
            }
            .distinctUntilChanged()
            .flatMapLatest {
                placeDao.getPlacesWithImagesStream(
                    useFilterPlaceIds = true,
                    filterPlaceIds = it
                )
            }

        return placesFlow.map { it.asModels() }
        //searchPlaceDao.searchPlaces(query).map { it.asModels() }
    }

    override fun getCount(): Flow<Int> {
        return searchPlaceDao.getPlacesCount()
    }
}