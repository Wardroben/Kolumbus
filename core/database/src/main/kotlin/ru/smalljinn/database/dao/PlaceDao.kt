package ru.smalljinn.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.PlaceEntity

@Dao
interface PlaceDao {
    @Upsert
    suspend fun upsertPlace(place: PlaceEntity): Long
    @Delete
    suspend fun deletePlace(place: PlaceEntity)
    @Query(
        """
            SELECT * FROM places
            ORDER BY favorite
        """
    )
    fun getPlacesStream(): Flow<List<PlaceEntity>>

    @Query(
        """
            SELECT * FROM places
            WHERE id LIKE :placeId
        """
    )
    suspend fun getPlaceById(placeId: Long): PlaceEntity
}