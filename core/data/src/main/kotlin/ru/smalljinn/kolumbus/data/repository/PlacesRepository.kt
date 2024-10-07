package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import ru.smalljinn.model.data.Place

interface PlacesRepository {
    fun getPlacesStream(): Flow<List<Place>>
    fun getPlace(placeId: Long?): Flow<Place?>
    suspend fun upsertPlace(place: Place): Long
    suspend fun deletePlace(place: Place)
    suspend fun deletePlaceById(placeId: Long)
}