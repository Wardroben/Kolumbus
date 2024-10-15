package ru.smalljinn.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.PlaceEntity
import ru.smalljinn.database.model.PlaceWithImages

@Dao
interface PlaceDao {
    @Upsert
    suspend fun upsertPlace(place: PlaceEntity): Long

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("""
        DELETE FROM places
        WHERE id LIKE :id
    """)
    suspend fun deletePlaceById(id: Long)

    @Query(
        """
            SELECT * FROM places
            ORDER BY favorite
        """
    )
    fun getPlacesStream(): Flow<List<PlaceEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM places
            WHERE id LIKE :placeId
        """
    )
    suspend fun getPlaceById(placeId: Long): PlaceWithImages

    @Transaction
    @Query("""
        SELECT * FROM places
    """)
    fun getPlacesWithImagesStream(): Flow<List<PlaceWithImages>>
}