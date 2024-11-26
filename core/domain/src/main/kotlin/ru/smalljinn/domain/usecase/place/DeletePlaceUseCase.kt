package ru.smalljinn.domain.usecase.place

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import javax.inject.Inject

class DeletePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val imageRepository: ImageRepository,
    @Dispatcher(KolumbusDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(placeId: Long) {
        withContext(ioDispatcher) {
            val place = placesRepository.getPlace(placeId)
            if (place.images.isNotEmpty()) place.images.forEach { image ->
                imageRepository.deleteImage(image)
            }
            placesRepository.deletePlaceById(placeId)
        }
    }
}