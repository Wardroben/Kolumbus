package ru.smalljinn.domain.usecase.place

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.domain.resource.ResourceManager
import ru.smalljinn.repository.FakeImageRepository
import ru.smalljinn.repository.FakePlaceRepository
import ru.smalljinn.util.ImageUtils
import ru.smalljinn.util.PlaceUtils
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
        val firstPlaceImages = ImageUtils.getTestImages(3)
        val newPlace = PlaceUtils.getTestPlace(images = firstPlaceImages)
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
