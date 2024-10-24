package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import ru.smalljinn.model.data.Place

interface SearchPlacesRepository {
    suspend fun populateFtsData()
    fun searchPlaces(query: String): Flow<List<Place>>
    fun getCount(): Flow<Int>
}