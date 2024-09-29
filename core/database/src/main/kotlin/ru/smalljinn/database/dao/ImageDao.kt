package ru.smalljinn.database.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.smalljinn.database.model.ImageEntity

@Dao
interface ImageDao {
    @Upsert
    suspend fun upsertImages(images: List<ImageEntity>)
    @Delete
    suspend fun deleteImages(images: List<ImageEntity>)
    @Query(
        """
            SELECT * FROM images
            WHERE place_id LIKE :placeId
        """
    )
    fun getPlaceImagesStream(placeId: Long): Flow<List<ImageEntity>>
    @Query(
        """
            SELECT uri FROM images
            WHERE id LIKE :imageId
        """
    )
    suspend fun getHeadingImageUri(imageId: Long): Uri
}