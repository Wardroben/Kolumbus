package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.flow.Flow
import ru.smalljinn.model.data.Place

interface SearchPlacesRepository {
    fun searchPlaces(query: String): Flow<List<Place>>
    fun getCount(): Flow<Int>
}