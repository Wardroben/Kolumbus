package ru.smalljinn.core.photo_store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "PhotoManager"
private const val WEBP_COMPRESSION_LEVEL = 70
private const val JPEG_COMPRESSION_LEVEL = 70
private const val TARGET_IMAGE_SIZE = 1024f

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
                    val scaledBitmap = scaleImage(originalBitmap)
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
                        scaledBitmap.compress(compressFormat, compressLevel, outputStream)
                    }

                    compressedImageUris.add(outputFile.toUri())

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

    /**
     * Scales images to TARGET size
     */
    private fun scaleImage(bitmap: Bitmap): Bitmap {
        with(bitmap) {
            val ratio: Float =
                if (width >= height) TARGET_IMAGE_SIZE / width else TARGET_IMAGE_SIZE / height
            val scaledWidth = (width * ratio).roundToInt()
            val scaledHeight = (height * ratio).roundToInt()

            val scaledBitmap = Bitmap.createScaledBitmap(
                this@with,
                scaledWidth,
                scaledHeight,
                true
            )
            return scaledBitmap
        }
    }

    override fun deletePhotoFromDevice(uri: Uri): Result<Unit, PhotoError> {
        try {
            val photoFile = uri.toFile()
            val isDeleted = photoFile.delete()
            if (isDeleted) Log.v(TAG, "Photo file DELETED: ${photoFile.path}")
            else Log.e(TAG, "Photo file IS NOT deleted: ${photoFile.path}")
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

    override fun getUriForTakePhoto(): Uri {
        val photoFile = PhotoFileProvider.createTemporaryFileForPhoto(context)
        return PhotoFileProvider.getUriForFile(photoFile, context)
    }
}