package ru.smalljinn.core.photo_store

import android.content.Context
import androidx.core.content.FileProvider
import kotlinx.datetime.Clock
import ru.smalljinn.photo_store.R
import java.io.File
import java.io.IOException

const val AUTHORITY = "ru.smalljinn.kolumbus.fileprovider"
const val IMAGES_PATH_NAME = "images"
const val TEMPORARY_IMAGES_PATH_NAME = "images"

class PhotoFileProvider : FileProvider(R.xml.file_paths) {
    companion object {
        fun getUriForFile(file: File, context: Context) =
            getUriForFile(context, AUTHORITY, file)

        fun createTemporaryFileForPhoto(context: Context): File {
            //require(checkOrCreatePhotoDir(context, TEMPORARY_IMAGES_PATH_NAME))
            val timeMillis = Clock.System.now().toEpochMilliseconds()
            try {
                val photoDirectory = File(context.cacheDir, "temp_images")
                if (!photoDirectory.exists()) photoDirectory.mkdirs()
                val photoFile = File.createTempFile("temp_image_$timeMillis", ".img", photoDirectory)
                photoFile.deleteOnExit()
                return photoFile
            } catch (e: IOException) {
                e.printStackTrace()
                throw IOException(e)
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e)
            }
        }

        fun createFileForPhoto(context: Context): File {
            require(checkOrCreatePhotoDir(context, IMAGES_PATH_NAME))
            val timeMillis = Clock.System.now().toEpochMilliseconds()
            try {
                val photoFile = File(context.filesDir, "$IMAGES_PATH_NAME/image_$timeMillis.img")
                photoFile.createNewFile()
                return photoFile
            } catch (e: IOException) {
                e.printStackTrace()
                throw IOException(e)
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e)
            }
        }
    }
}

private fun checkOrCreatePhotoDir(context: Context, directoryName: String): Boolean {
    try {
        val fileDirectory = File(context.filesDir, directoryName)
        if (!fileDirectory.exists()) return fileDirectory.mkdirs()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}
