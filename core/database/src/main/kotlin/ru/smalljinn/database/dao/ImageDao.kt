package ru.smalljinn.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.ImageEntity

@Dao
interface ImageDao {
    @Upsert
    suspend fun insertImages(images: List<ImageEntity>): List<Long>

    @Query("""
        DELETE FROM images
        WHERE image_id LIKE :imageId
    """)
    suspend fun deleteImageById(imageId: Long)

    @Query(
        """
            SELECT * FROM images
            WHERE place_id LIKE :placeId
        """
    )
    fun getPlaceImagesStream(placeId: Long): Flow<List<ImageEntity>>
}