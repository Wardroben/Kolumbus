package ru.smalljinn.kolumbus.data.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.core.photo_store.PhotoManagerImpl
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.model.asModels
import ru.smalljinn.kolumbus.data.model.asImageEntities
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

private const val TAG = "ImagesRepo"

class OfflineImagesRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val photoManager: PhotoManagerImpl
) : ImageRepository {
    override suspend fun insertImages(
        imageUris: List<Uri>,
        placeId: Long
    ): Result<Unit, PhotoError> {
        return withContext(Dispatchers.IO) {
            when (val compressedImageUrisOnDevice = photoManager.savePhotosToDevice(imageUris)) {
                is Result.Error -> {
                    Log.e(TAG, "Images not saved: ${compressedImageUrisOnDevice.error.name}")
                    return@withContext Result.Error(compressedImageUrisOnDevice.error)
                }
                is Result.Success -> {
                    imageDao.insertImages(
                        compressedImageUrisOnDevice.data.asImageEntities(placeId)
                    )
                    Log.v(
                        TAG,
                        "Image successfully inserted: ${compressedImageUrisOnDevice.data.size} count"
                    )
                }
            }
            Result.Success(Unit)
        }

    }

    override suspend fun deleteImage(image: Image): Result<Unit, PhotoError> {
        when (val deleteResult = photoManager.deletePhotoFromDevice(image.url.toUri())) {
            is Result.Error -> {
                Log.e(TAG, "image not DELETED: ${deleteResult.error.name}")
                return deleteResult
            }
            is Result.Success -> {
                imageDao.deleteImageById(image.id)
                Log.v(
                    TAG,
                    "Image successfully DELETED: ${image.url}"
                )
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun getPlaceImages(placeId: Long): List<Image> {
        return imageDao.getPlaceImages(placeId).asModels()
    }
}