package ru.smalljinn.domain.usecase.place

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.model.data.Place
import ru.smalljinn.repository.FakeImageRepository
import ru.smalljinn.repository.FakePlaceRepository
import ru.smalljinn.util.ImageUtils
import ru.smalljinn.util.PlaceUtils
import kotlin.test.assertEquals

class DeletePlaceUseCaseTest {
    private lateinit var deletePlaceUseCase: DeletePlaceUseCase
    private lateinit var placeRepository: PlacesRepository
    private lateinit var imageRepository: ImageRepository
    private lateinit var places: List<Place>

    @Before
    fun setUp() {
        placeRepository = FakePlaceRepository()
        imageRepository = FakeImageRepository()
        deletePlaceUseCase = DeletePlaceUseCase(
            placesRepository = placeRepository,
            imageRepository = imageRepository
        )
        val firstPlaceImages = ImageUtils.getTestImages(0..3)
        val secondPlaceImages = ImageUtils.getTestImages(23..25)
        places = listOf(
            PlaceUtils.getTestPlace(id = 0, images = firstPlaceImages),
            PlaceUtils.getTestPlace(id = 1, headerImageId = 24, images = secondPlaceImages),
        )
        places.forEach { place -> runBlocking { placeRepository.upsertPlace(place) } }
    }

    @Test
    fun `Use case deletes place correctly`() {
        runBlocking {
            deletePlaceUseCase(0)
            val deletedPlace = placeRepository.getPlace(0)
            assertEquals(9999, deletedPlace.id)
        }

    }
}