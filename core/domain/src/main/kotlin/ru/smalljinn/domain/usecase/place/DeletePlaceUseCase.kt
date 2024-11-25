package ru.smalljinn.domain.usecase.place

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
            val place = placesRepository.getPlace(placeId)
            if (place.images.isNotEmpty()) place.images.forEach { image ->
                imageRepository.deleteImage(image)
            }
            placesRepository.deletePlaceById(placeId)
        }
    }
}