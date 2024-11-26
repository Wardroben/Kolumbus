package ru.smalljinn.domain.usecase.place

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.R
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.domain.resource.ResourceManager
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

private const val TAG = "SavePlaceUC"

class SavePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val imagesRepository: ImageRepository,
    private val resourceManager: ResourceManager,
    @Dispatcher(KolumbusDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
) {
    @Throws(InvalidPlaceException::class)
    suspend operator fun invoke(
        place: Place,
        imagesToDelete: Set<Image>,
        compressImages: Boolean,
        isImporting: Boolean
    ): Long {
        if (place.title.isBlank()) throw InvalidPlaceException(
            resourceManager.getString(R.string.empty_title_exception)
        )
        if (place.position.isInit) throw InvalidPlaceException(
            resourceManager.getString(R.string.invalid_position_exception)
        )
        //if (place.isPlaceInvalid) throw InvalidPlaceException(R.string.invalid_position_exception)

        return withContext(ioDispatcher) {
            val placeId = if (place.id == Place.CREATION_ID) 0L else place.id
            val imagesToInsert = place.images.filter { it.id == 0L }
            val placeToInsert = place.copy(id = placeId)

            if (imagesToDelete.isNotEmpty()) imagesToDelete.forEach { image ->
                when (val result = imagesRepository.deleteImage(image)) {
                    is Result.Error -> Log.e(TAG, "Image $image IS NOT deleted: ${result.error}")
                    is Result.Success -> Log.v(TAG, "Image $image successfully deleted")
                }
            }
            val insertPlaceResultId = placesRepository.upsertPlace(placeToInsert)
            imagesRepository.insertImages(
                imagesUri = imagesToInsert.map { it.url },
                placeId = if (insertPlaceResultId == -1L) place.id else insertPlaceResultId,
                compress = compressImages,
                imageFilesAlreadyExists = isImporting
            )
            insertPlaceResultId
        }
    }
}

class InvalidPlaceException(message: String) : IllegalStateException(message)
