package ru.smalljinn.domain.usecase.place

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.Before
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.domain.resource.ResourceManager
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position
import ru.smalljinn.repository.FakeImageRepository
import ru.smalljinn.repository.FakePlaceRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class SavePlaceUseCaseTest {
    private lateinit var placeRepository: PlacesRepository
    private lateinit var imagesRepository: ImageRepository
    private lateinit var resourceManager: ResourceManager

    private lateinit var savePlaceUseCase: SavePlaceUseCase

    @Before
    fun setUp() {
        placeRepository = FakePlaceRepository()
        imagesRepository = FakeImageRepository()
        resourceManager = FakeResourceManager()
        savePlaceUseCase = SavePlaceUseCase(placeRepository, imagesRepository, resourceManager)
    }

    @Test
    fun `Use case successfully saves place`() {
        val firstPlaceImages = listOf(
            Image(0, "dummyUri"),
            Image(1, "dummyUri1"),
            Image(2, "dummyUri2"),
        )
        val newPlace = Place(
            id = 0,
            title = "dummyTitle",
            description = "dummyDescription",
            position = Position(latitude = 2.55, longitude = 3.45),
            creationDate = Clock.System.now(),
            headerImageId = null,
            favorite = false,
            images = firstPlaceImages
        )
        runBlocking {
            savePlaceUseCase(
                newPlace,
                imagesToDelete = emptySet(),
                compressImages = false,
                isImporting = false
            )
            val allPlaces = placeRepository.getPlacesStream().first()
            assertEquals(allPlaces.first(), newPlace)
        }
    }
}

class FakeResourceManager : ResourceManager {
    private val strings = mapOf(1 to "Some words", 2 to "Foo", 3 to "Fake")
    override fun getString(id: Int, vararg formatArgs: Any): String {
        return strings[id] + formatArgs.toString()
    }
}