package ru.smalljinn.kolumbus.data.image

import android.graphics.Bitmap
import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.image.ImageCompressor
import ru.smalljinn.domain.image.ImageFormat
import ru.smalljinn.domain.image.ImageScaler
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val TARGET_IMAGE_DENSITY = 256

class AndroidImageCompressor @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    @Dispatcher(KolumbusDispatcher.Default) private val defaultDispatcher: CoroutineDispatcher
): ImageCompressor<Bitmap> {
    override suspend fun compressImage(image: Bitmap, imageFormat: ImageFormat): ByteArray {
        return withContext(defaultDispatcher) {
            val scaledBitmap = imageScaler.scaleImage(image, TARGET_IMAGE_DENSITY) //scaleImage(image, TARGET_IMAGE_SIZE)
            val compressFormat = determineCompressFormat(imageFormat)

            var outputBytes: ByteArray

            ByteArrayOutputStream().use { outputStream ->
                scaledBitmap.compress(compressFormat, 60, outputStream)
                outputBytes = outputStream.toByteArray()
            }

            outputBytes
        }
    }
    private fun determineCompressFormat(mimeType: ImageFormat) = when (mimeType) {
        ImageFormat.Jpeg -> Bitmap.CompressFormat.JPEG
        ImageFormat.Png -> Bitmap.CompressFormat.PNG
        ImageFormat.Webp -> if (Build.VERSION.SDK_INT >= 30) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else Bitmap.CompressFormat.WEBP

        else -> Bitmap.CompressFormat.JPEG
    }
    private fun determineCompressFormat(mimeType: String) = when (mimeType) {
        "image/png" -> Bitmap.CompressFormat.PNG
        "image/jpeg" -> Bitmap.CompressFormat.JPEG
        "image/webp" -> if (Build.VERSION.SDK_INT >= 30) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else Bitmap.CompressFormat.WEBP

        else -> Bitmap.CompressFormat.JPEG
    }
}