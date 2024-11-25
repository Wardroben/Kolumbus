package ru.smalljinn.domain.usecase.place

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

import org.junit.Before
import org.junit.Test
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position
import ru.smalljinn.repository.FakeImageRepository
import ru.smalljinn.repository.FakePlaceRepository
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
        val firstPlaceImages = listOf(
            Image(0, "dummyUri"),
            Image(1, "dummyUri1"),
            Image(2, "dummyUri2"),
        )
        val secondPlaceImages = listOf(
            Image(23, "1dummyUri"),
            Image(24, "1dummyUri1"),
            Image(26, "1dummyUri2"),
        )
        places = listOf(
            Place(
                id = 0,
                title = "dummyTitle",
                description = "dummyDescription",
                position = Position.initialPosition(),
                creationDate = Clock.System.now(),
                headerImageId = null,
                favorite = false,
                images = firstPlaceImages
            ),
            Place(
                id = 1,
                title = "dummyTitle",
                description = "dummyDescription",
                position = Position.initialPosition(),
                creationDate = Clock.System.now(),
                headerImageId = 24,
                favorite = false,
                images = secondPlaceImages
            ),
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