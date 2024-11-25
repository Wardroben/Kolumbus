package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.model.asModel
import ru.smalljinn.database.model.asModels
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.model.asEntity
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import javax.inject.Inject

class OfflinePlacesRepository @Inject constructor(private val placeDao: PlaceDao) :
    PlacesRepository {
    override fun getPlacesStream(): Flow<List<Place>> {
        return placeDao.getPlacesWithImagesStream().map { it.asModels() }
    }

    override suspend fun getPlace(placeId: Long): Place {
        return placeDao.getPlaceById(placeId).asModel()
    }

    override suspend fun upsertPlace(place: Place): Long {
        val placeId = placeDao.upsertPlace(place.asEntity())
        return placeId
    }

    override suspend fun deletePlace(place: Place) {
        return placeDao.deletePlace(place.asEntity())
    }

    override suspend fun deletePlaceById(placeId: Long) {
        return placeDao.deletePlaceById(placeId)
    }

    override suspend fun makePlaceFavorite(placeId: Long, favorite: Boolean) {
        return placeDao.makeFavoritePlace(placeId, favorite)
    }

    override suspend fun getPlaceImages(placeId: Long): List<Image> =
        placeDao.getPlaceImages(placeId).map { it.asModel() }
}