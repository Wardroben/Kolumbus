import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.smalljinn.database.KolumbusDatabase
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.model.ImageEntity
import ru.smalljinn.database.model.PlaceEntity
import ru.smalljinn.model.data.Position
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PlaceDaoTest {
    private lateinit var placeDao: PlaceDao
    private lateinit var imageDao: ImageDao
    private lateinit var db: KolumbusDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            KolumbusDatabase::class.java
        ).build()
        placeDao = db.placeDao()
        imageDao = db.imageDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun placeDao_get_item_with_header_image() = runTest {
        val placeEntities = listOf(
            testPlaceEntity(1,null),
            testPlaceEntity(2,1),
            testPlaceEntity(3, null)
        )
        val placeImageEntities = listOf(
            testImageEntity(1, 1),
            testImageEntity(12,1),
            testImageEntity(14,2),
            testImageEntity(15,2),
        )
        placeEntities.forEach { place ->
            placeDao.upsertPlace(place)
        }
        imageDao.insertImages(placeImageEntities)

        val place = placeDao.getPlaceById(2)
        assertNotEquals(illegal = null, actual = place.placeEntity.headerImageId)
    }

    @Test
    fun placeDao_get_images_of_place() = runTest {
        val placeEntityWithImages = testPlaceEntity(id = 5, headerImageId = null)
        val placeEntityNoImages = testPlaceEntity(id = 6, headerImageId = 1)

        placeDao.upsertPlace(placeEntityWithImages)
        placeDao.upsertPlace(placeEntityNoImages)

        val placeImageEntities = listOf(
            testImageEntity(1,5),
            testImageEntity(2,5),
            testImageEntity(4,5),
            testImageEntity(3,5),
            testImageEntity(15,6),
            testImageEntity(25, 6),
        )
        imageDao.insertImages(placeImageEntities)

        val placeImages = imageDao.getPlaceImagesStream(placeEntityWithImages.id).first()
        val placeImagesCount = placeImageEntities.filter { it.placeId == placeEntityWithImages.id }.size
        assertEquals(placeImagesCount, placeImages.size)
    }
}

private fun testPlaceEntity(id: Long, headerImageId: Long?) =
    PlaceEntity(
        id = id,
        title = "",
        description = "",
        position = Position(0.0,0.0),
        creationDate = Instant.fromEpochMilliseconds(0),
        headerImageId = headerImageId,
        favorite = false
    )

private fun testImageEntity(id: Long, placeId: Long) =
    ImageEntity(
        imageId = id,
        uri = "datasource://path.image$id",
        placeId = placeId
    )