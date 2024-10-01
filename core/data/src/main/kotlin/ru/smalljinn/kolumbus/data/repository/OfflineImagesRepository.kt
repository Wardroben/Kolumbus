package ru.smalljinn.kolumbus.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.smalljinn.core.photo_store.PhotoManagerImpl
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.model.asModels
import ru.smalljinn.kolumbus.data.model.asImageEntities
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

class OfflineImagesRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val photoManager: PhotoManagerImpl
): ImageRepository {
    override suspend fun insertImages(imageUris: List<Uri>, placeId: Long): Result<Unit, PhotoError> {
        when(val compressedImageUrisOnDevice = photoManager.savePhotosToDevice(imageUris)) {
            is Result.Error -> return Result.Error(compressedImageUrisOnDevice.error)
            is Result.Success -> imageDao.insertImages(compressedImageUrisOnDevice.data.asImageEntities(placeId))
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteImage(image: Image): Result<Unit, PhotoError> {
        when(val deleteResult = photoManager.deletePhotoFromDevice(image.url)) {
            is Result.Error -> return deleteResult
            is Result.Success -> imageDao.deleteImageById(image.id)
        }
        return Result.Success(Unit)
    }

    override fun getPlaceImagesStream(placeId: Long): Flow<List<Image>> {
        return imageDao.getPlaceImagesStream(placeId).map { imageEntities ->
            imageEntities.asModels()
        }
    }
}