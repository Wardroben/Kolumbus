package ru.smalljinn.core.photo_store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

private const val TAG = "PhotoManager"
private const val WEBP_COMPRESSION_LEVEL = 50
private const val JPEG_COMPRESSION_LEVEL = 70

class PhotoManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoManager {
    override fun savePhotosToDevice(uris: List<Uri>): Result<List<Uri>, PhotoError> {
        if (uris.isEmpty()) return Result.Error(PhotoError.EMPTY_URIS)
        val contentResolver = context.contentResolver
        val compressedImageUris = mutableListOf<Uri>()
        uris.forEach { uri ->
            try {
                contentResolver.openInputStream(uri).use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        ?: return Result.Error(PhotoError.DECODE_FAILED)
                    val outputFile = PhotoFileProvider.createFileForPhoto(context)

                    FileOutputStream(outputFile).use { outputStream ->
                        val compressFormat = when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                                Bitmap.CompressFormat.WEBP_LOSSY

                            else -> Bitmap.CompressFormat.JPEG
                        }
                        val compressLevel =
                            if (compressFormat == Bitmap.CompressFormat.JPEG) JPEG_COMPRESSION_LEVEL
                            else WEBP_COMPRESSION_LEVEL
                        originalBitmap.compress(compressFormat, compressLevel, outputStream)
                    }

                    compressedImageUris.add(PhotoFileProvider.getUriForFile(outputFile, context))

                    Log.v(
                        TAG,
                        "Photo successfully compressed and saved in file: ${outputFile.path}"
                    )
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
        return Result.Success(compressedImageUris.toList())
    }

    override fun deletePhotoFromDevice(url: String): Result<Unit, PhotoError> {
        if (url.isBlank()) return Result.Error(PhotoError.EMPTY_URIS)
        try {
            val photoFile = File(url)
            if (photoFile.exists() && photoFile.isFile) {
                photoFile.delete()
            } else Log.e(TAG, "Can't delete file ${photoFile.path}")
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return Result.Error(PhotoError.FILE_NOT_FOUND)
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.Error(PhotoError.HAVE_NOT_ACCESS)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(PhotoError.UNKNOWN)
        }
        return Result.Success(Unit)
    }
}