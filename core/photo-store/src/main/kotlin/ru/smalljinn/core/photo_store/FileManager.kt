package ru.smalljinn.core.photo_store

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import java.io.FileOutputStream
import javax.inject.Inject

class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImage(bytes: ByteArray): Uri {
        return withContext(Dispatchers.IO) {
            val file = PhotoFileProvider.createFileForPhoto(context)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(bytes)
            }

            PhotoFileProvider.getUriForFile(file, context)
        }
    }

    suspend fun deleteImage(uri: Uri): Result<Unit, PhotoError> {
        return withContext(Dispatchers.IO) {
            val deletedRows = context.contentResolver.delete(uri, null, null)
            if (deletedRows == 1) Result.Success(Unit) else Result.Error(PhotoError.FILE_NOT_DELETED)
        }
    }

    suspend fun clearTempImages() {
        withContext(Dispatchers.IO) {
            val cacheDirectory = context.cacheDir
            if (cacheDirectory.exists() && cacheDirectory.isDirectory) {
                val files = cacheDirectory.listFiles()
                if (files != null && files.isNotEmpty()) {
                    files.forEach { file ->
                        file.delete()
                    }
                }
            }
        }
    }
}