package ru.smalljinn.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place

interface PlacesRepository {
    fun getPlacesStream(): Flow<List<Place>>
    suspend fun getPlace(placeId: Long): Place
    suspend fun upsertPlace(place: Place): Long
    suspend fun deletePlace(place: Place)
    suspend fun deletePlaceById(placeId: Long)
    suspend fun makePlaceFavorite(placeId: Long, favorite: Boolean)
    suspend fun getPlaceImages(placeId: Long): List<Image>
}