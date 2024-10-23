package ru.smalljinn.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.PlaceWithImages

@Dao
interface SearchPlaceDao {
    @Transaction
    @Query("""
        SELECT * FROM places 
        WHERE title LIKE :query OR description LIKE :query
    """) //TODO replace LIKE with MATCH
    fun searchPlaces(query: String): Flow<List<PlaceWithImages>>

    @Query("""
        SELECT count(*) from places
    """)
    fun getPlacesCount(): Flow<Int>
}