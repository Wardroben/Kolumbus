package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.model.asModel
import ru.smalljinn.database.model.asModels
import ru.smalljinn.kolumbus.data.model.asEntities
import ru.smalljinn.kolumbus.data.model.asEntity
import ru.smalljinn.model.data.Place
import javax.inject.Inject

class OfflinePlacesRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val imageDao: ImageDao
): PlacesRepository {
    override fun getPlacesStream(): Flow<List<Place>> {
        return placeDao.getPlacesWithImagesStream().map { it.asModels() }
    }

    override fun getPlace(placeId: Long?): Flow<Place?> {
        if (placeId == null) return emptyFlow()
        return placeDao.getPlaceById(placeId).map { placeWithImages ->
            val place = placeWithImages.asModel()
            place
        }
    }

    override suspend fun upsertPlace(place: Place): Long {
        val placeId = placeDao.upsertPlace(place.asEntity())
        val imageEntities = place.images.asEntities(placeId)
        imageDao.insertImages(imageEntities)
        return placeId
    }

    override suspend fun deletePlace(place: Place) {
        return placeDao.deletePlace(place.asEntity())
    }

    override suspend fun deletePlaceById(placeId: Long) {
        return placeDao.deletePlaceById(placeId)
    }
}