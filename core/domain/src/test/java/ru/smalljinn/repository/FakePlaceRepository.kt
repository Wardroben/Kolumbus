package ru.smalljinn.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place

class FakePlaceRepository: PlacesRepository {
    private val places = arrayListOf<Place>()

    override fun getPlacesStream(): Flow<List<Place>> {
        return flow { emit(places) }
    }

    override suspend fun getPlace(placeId: Long): Place {
        return findPlace(placeId) ?: Place.getInitPlace().copy(id = 9999)
    }

    override suspend fun upsertPlace(place: Place): Long {
        places.add(place)
        return places.lastIndex.toLong()
    }

    override suspend fun deletePlace(place: Place) {
        places.remove(place)
    }

    override suspend fun deletePlaceById(placeId: Long) {
        val placeToDelete = findPlace(placeId) ?: return
        places.remove(placeToDelete)
    }

    override suspend fun makePlaceFavorite(placeId: Long, favorite: Boolean) {
         val place = findPlace(placeId) ?: return
         val index = places.indexOf(place)
         places[index] = place.copy(favorite = favorite)
    }

    override suspend fun getPlaceImages(placeId: Long): List<Image> {
        return findPlace(placeId)?.images ?: emptyList()
    }

    private fun findPlace(placeId: Long): Place? = places.find { it.id == placeId }
}