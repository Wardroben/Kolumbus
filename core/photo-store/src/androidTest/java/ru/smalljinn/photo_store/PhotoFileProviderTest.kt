package ru.smalljinn.photo_store

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.smalljinn.core.photo_store.IMAGES_PATH_NAME
import ru.smalljinn.core.photo_store.PhotoFileProvider
import java.io.File
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class PhotoFileProviderTest {
    private lateinit var context: Context
    private val testAuthority = "test.authority"

    @Before
    fun setContext() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun photoFileProvider_gets_uri_for_new_file() {
        val testFile = File(context.cacheDir, "test_file.md")
        try {
            val fileNotExistsBeforeCreation = testFile.createNewFile()
            assertEquals(true, fileNotExistsBeforeCreation)

            val uri: Uri? = PhotoFileProvider.getUriForFile(testFile, context)
            assertEquals(true, uri != null)
            assertEquals("content", uri?.scheme)
            assertEquals(testAuthority, uri?.authority)
        } catch (e: Exception) {

        } finally {
            testFile.delete()
        }
    }

    @Test
    fun photoFileProvider_creates_file_for_photo() {
        val photoFile = PhotoFileProvider.createFileForPhoto(context)
        try {
            assertEquals(true, photoFile.exists())
            assertEquals("${context.filesDir}/$IMAGES_PATH_NAME/image", photoFile.absolutePath.substringBeforeLast('_'))
        } catch (e: Exception) {

        } finally {
            photoFile.delete()
        }

    }

}