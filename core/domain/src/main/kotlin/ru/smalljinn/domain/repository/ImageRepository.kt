package ru.smalljinn.domain.repository

import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result

interface ImageRepository {
    suspend fun insertImages(
        imagesUri: List<String>,
        placeId: Long,
        compress: Boolean,
        imageFilesAlreadyExists: Boolean
    ): Result<Unit, PhotoError>

    suspend fun deleteImage(image: Image): Result<Unit, PhotoError>
}