package ru.smalljinn.kolumbus.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.model.ImageEntity
import ru.smalljinn.domain.image.ImageCompressor
import ru.smalljinn.domain.image.ImageGetter
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.kolumbus.data.image.AndroidImageSaver
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

class OfflineImagesRepository @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageSaver: AndroidImageSaver, //TODO()
    private val imageDao: ImageDao
) : ImageRepository {
    override suspend fun insertImages(
        imagesUri: List<String>,
        placeId: Long,
        compress: Boolean,
        imageFilesAlreadyExists: Boolean
    ): Result<Unit, PhotoError> {
        //Если изображения уже в папке приложения (в случае импорта), то добавить записи в БД
        if (imageFilesAlreadyExists) {
            imageDao.insertImages(imagesUri.map { ImageEntity(0, it, placeId) })
            return Result.Success(Unit)
        }

        //В ином случае читаем изображения по Uri, сохраняем в filesDir и добавляем в БД
        val saveImagesResult = imagesUri.map { uri: String -> saveImage(uri.toUri(), compress) }

        val imagesToAdd = saveImagesResult
            .filterIsInstance<Result.Success<Uri, PhotoError>>() //filter success
            .map { result -> ImageEntity(0, result.data.toString(), placeId) }

        imageDao.insertImages(imagesToAdd)

        return if (saveImagesResult.any { it is Result.Error }) Result.Error(PhotoError.SOME_IMAGES_NOT_SAVED)
        else Result.Success(Unit)
    }

    private suspend fun saveImage(uri: Uri, compress: Boolean): Result<Uri, PhotoError> {
        when (val dataResult = fileController.readBytes(uri.toString())) {
            is Result.Error -> return Result.Error(PhotoError.DECODE_FAILED)
            is Result.Success -> {
                val imageUri = if (compress) {
                    val bitmap = imageGetter.getImage(uri.toString()) ?: return Result.Error(
                        PhotoError.DECODE_FAILED
                    )
                    val compressedData = imageCompressor.compressImage(
                        bitmap,
                        imageGetter.getImageFormat(uri.toString())
                    )
                    imageSaver.saveImageToFilesDir(compressedData)
                } else {
                    imageSaver.saveImageToFilesDir(dataResult.data)
                }
                return Result.Success(imageUri)
            }
        }
    }

    override suspend fun deleteImage(image: Image): Result<Unit, PhotoError> {
        imageDao.deleteImageById(image.id)
        return when (val deleteResult = fileController.deleteFile(image.url)) {
            is Result.Error -> Result.Error(PhotoError.FILE_NOT_DELETED)
            is Result.Success -> Result.Success(Unit)
        }
    }

}