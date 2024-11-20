package ru.smalljinn.kolumbus.data.image

import android.content.Context
import androidx.core.content.FileProvider
import kotlinx.datetime.Clock
import ru.smalljinn.data.R
import java.io.File
import java.io.IOException

const val AUTHORITY = "ru.smalljinn.kolumbus.fileprovider"
const val IMAGES_PATH_NAME = "images"
const val TEMPORARY_IMAGES_PATH_NAME = "temp_images"

class ImageFileProvider : FileProvider(R.xml.file_paths) {
    companion object {
        fun getUriForFile(file: File, context: Context) =
            getUriForFile(context, AUTHORITY, file)

        fun createTemporaryFileForImage(context: Context): File {
            //require(checkOrCreateImageDir(context, TEMPORARY_IMAGES_PATH_NAME))
            val timeMillis = Clock.System.now().toEpochMilliseconds()
            try {
                val imageDirectory = File(context.cacheDir, TEMPORARY_IMAGES_PATH_NAME)
                if (!imageDirectory.exists()) imageDirectory.mkdirs()
                val imageFile = File.createTempFile("temp_image_$timeMillis", ".img", imageDirectory)
                imageFile.deleteOnExit()
                return imageFile
            } catch (e: IOException) {
                e.printStackTrace()
                throw IOException(e)
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e)
            }
        }

        fun createFileForImage(context: Context, fileName: String): File {
            require(checkOrCreateImageDir(context))
            val timeMillis = Clock.System.now().toEpochMilliseconds()
            try {
                val imageDirectory = File(context.filesDir, IMAGES_PATH_NAME)
                val imageFile = File(imageDirectory, fileName)
                imageFile.createNewFile()
                return imageFile
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

private fun checkOrCreateImageDir(context: Context): Boolean {
    try {
        val fileDirectory = File(context.filesDir, IMAGES_PATH_NAME)
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
