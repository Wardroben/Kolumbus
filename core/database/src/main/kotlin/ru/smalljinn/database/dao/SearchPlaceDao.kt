package ru.smalljinn.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.PlaceFtsEntity

private typealias PlaceFtsId = Int

@Dao
interface SearchPlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceFtsEntity>)

    @Query("""
        SELECT rowid FROM places_fts 
        WHERE places_fts MATCH :query
    """)
    fun searchPlaces(query: String): Flow<List<PlaceFtsId>>

    @Query("SELECT count(*) from places_fts")
    fun getPlacesCount(): Flow<Int>
}