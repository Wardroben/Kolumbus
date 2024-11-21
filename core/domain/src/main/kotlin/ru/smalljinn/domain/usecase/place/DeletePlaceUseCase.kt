package ru.smalljinn.domain.usecase.place

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import javax.inject.Inject

private const val TAG = "DeletePlaceUC"

class DeletePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(placeId: Long) {
        withContext(Dispatchers.IO) {
            val placeImages = imageRepository.getPlaceImages(placeId)
            if (placeImages.isNotEmpty()) placeImages.forEach { image ->
                imageRepository.deleteImage(image)
            }
            placesRepository.deletePlaceById(placeId)
        }
        Log.v(TAG, "Place with $placeId id deleted")
    }
}