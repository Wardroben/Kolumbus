package ru.smalljinn.repository

import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result

class FakeImageRepository : ImageRepository {
    private val images = mutableListOf<Image>()

    override suspend fun insertImages(
        imagesUri: List<String>,
        placeId: Long,
        compress: Boolean,
        imageFilesAlreadyExists: Boolean
    ): Result<Unit, PhotoError> {
        val imagesToAdd = imagesUri.mapIndexed { index, uri ->
            Image(
                id = images.lastIndex + index.toLong(),
                url = uri,
            )
        }
        images.addAll(imagesToAdd)
        return Result.Success(Unit)
    }

    override suspend fun deleteImage(image: Image): Result<Unit, PhotoError> {
        return if (images.remove(image)) Result.Success(Unit)
        else Result.Error(PhotoError.UNKNOWN)
    }
}