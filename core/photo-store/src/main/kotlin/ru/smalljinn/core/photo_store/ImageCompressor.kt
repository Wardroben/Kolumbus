package ru.smalljinn.core.photo_store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TARGET_IMAGE_SIZE = 1024f

class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun compressImage(
        uri: Uri
    ) : Result<ByteArray, PhotoError> {
        val contentResolver = context.contentResolver
        return withContext(Dispatchers.IO) {
            val mimeType = contentResolver.getType(uri)
            val inputBytes = contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: return@withContext Result.Error(PhotoError.DECODE_FAILED)

            ensureActive()

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                val scaledBitmap = scaleImage(bitmap)

                val compressFormat = when(mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if (Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSY
                    } else Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }

                var outputBytes: ByteArray

                ByteArrayOutputStream().use { outputStream ->
                    scaledBitmap.compress(compressFormat, 60, outputStream)
                    outputBytes = outputStream.toByteArray()
                }

                Result.Success(outputBytes)
            }
        }
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
}