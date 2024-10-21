package ru.smalljinn.core.photo_store

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "PhotoManager"


class PhotoManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileManager: FileManager,
    private val imageCompressor: ImageCompressor
) : PhotoManager {
    override suspend fun savePhotosToDevice(uris: List<Uri>): Result<List<Uri>, PhotoError> {
        if (uris.isEmpty()) return Result.Error(PhotoError.EMPTY_URIS)
        val compressedImageUris = mutableListOf<Uri>()
        uris.forEach { uri ->
            try {
                when (val result = imageCompressor.compressImage(uri)) {
                    is Result.Error -> Log.e(TAG, "Error compressing image: ${result.error}")
                    is Result.Success -> {
                        val savedFileUri = fileManager.saveImage(result.data)
                        compressedImageUris.add(savedFileUri)
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return Result.Error(PhotoError.FILE_NOT_FOUND)
            } catch (e: IOException) {
                e.printStackTrace()
                return Result.Error(PhotoError.HAVE_NOT_ACCESS)
            } catch (e: Exception) {
                e.printStackTrace()
                return Result.Error(PhotoError.UNKNOWN)
            }
        }
        fileManager.clearTempImages()
        return Result.Success(compressedImageUris.toList())
    }

    override suspend fun deletePhotoFromDevice(uri: Uri): Result<Unit, PhotoError> {
        return fileManager.deleteImage(uri)
    }

    override fun getUriForTakePhoto(): Uri {
        val photoFile = PhotoFileProvider.createTemporaryFileForPhoto(context)
        return PhotoFileProvider.getUriForFile(photoFile, context)
    }
}