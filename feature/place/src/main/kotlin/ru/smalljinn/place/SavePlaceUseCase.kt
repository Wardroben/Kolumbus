package ru.smalljinn.place

import android.util.Log
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.kolumbus.data.repository.ImageRepository
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

private const val TAG = "SavePlaceUC"


class SavePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val imagesRepository: ImageRepository,
) {
    @Throws(InvalidPlaceException::class)
    suspend operator fun invoke(place: Place, imagesToDelete: Set<Image>): Long {
        if (place.title.isBlank()) throw InvalidPlaceException(R.string.empty_title_exception)
        //if (place.isPlaceInvalid) throw InvalidPlaceException(R.string.invalid_position_exception)

        return withContext(Dispatchers.IO) {
            val placeId = if (place.id == Place.CREATION_ID) 0L else place.id
            val placeToInsert =
                place.copy(id = placeId, images = place.images.filter { it.id == 0L })
            if (imagesToDelete.isNotEmpty()) imagesToDelete.forEach { image ->
                when (val result = imagesRepository.deleteImage(image)) {
                    is Result.Error -> Log.e(TAG, "Image $image IS NOT deleted: ${result.error}")
                    is Result.Success -> Log.v(TAG, "Image $image successfully deleted")
                }
            }
            val insertPlaceResultId = placesRepository.upsertPlace(placeToInsert)
            insertPlaceResultId
        }
    }
}

class InvalidPlaceException(@StringRes val messageId: Int): Exception(messageId.toString())