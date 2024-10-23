package ru.smalljinn.photo_store

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith
import ru.smalljinn.core.photo_store.FileManager
import ru.smalljinn.core.photo_store.ImageCompressor
import ru.smalljinn.core.photo_store.PhotoManager
import ru.smalljinn.core.photo_store.PhotoManagerImpl

@RunWith(AndroidJUnit4::class)
class PhotoManagerTest {
    private lateinit var context: Context
    private lateinit var photoManager: PhotoManager
    private lateinit var testImageUris: List<Uri>

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        photoManager = PhotoManagerImpl(context, fileManager = FileManager(context), imageCompressor = ImageCompressor(context))
        testImageUris = listOf(
            Uri.parse("android.resource://${context.packageName}/drawable/m1000x1000.jpeg"),
            Uri.parse("android.resource://${context.packageName}/drawable/walter_white_skate.jpg")
        )
    }

    /*@Test
    fun photoManager_returns_error_if_uris_empty() {
        val emptyUriList: List<Uri> = emptyList()
        val result = photoManager.savePhotosToDevice(emptyUriList)
        assertEquals(PhotoError.EMPTY_URIS, (result as Result.Error).error)
    }

    @Test
    fun photoManager_saved_image_to_storage() {
        val result = photoManager.savePhotosToDevice(testImageUris)

        assertEquals(testImageUris.size, (result as Result.Success).data.size)
    }*/
}