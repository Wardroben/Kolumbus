package ru.smalljinn.kolumbus.data.image

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.domain.image.ImageScaler
import javax.inject.Inject

class AndroidImageScaler @Inject constructor(): ImageScaler<Bitmap> {
    override suspend fun scaleImage(image: Bitmap, imageSize: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            with(image) {
                /*val ratio: Float =
                    if (width >= height) imageSize.toFloat() / width else imageSize.toFloat() / height
                val scaledWidth = (width * ratio).roundToInt()
                val scaledHeight = (height * ratio).roundToInt()*/

                val scaledWidth = image.getScaledWidth(imageSize)
                val scaledHeight = image.getScaledHeight(imageSize)

                val scaledBitmap = image.scale(scaledWidth, scaledHeight)

                /*val scaledBitmap = Bitmap.createScaledBitmap(
                    this@with,
                    scaledWidth,
                    scaledHeight,
                    true
                )*/

                scaledBitmap
            }
        }
    }
}