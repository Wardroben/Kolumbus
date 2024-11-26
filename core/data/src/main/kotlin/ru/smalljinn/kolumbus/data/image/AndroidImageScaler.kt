package ru.smalljinn.kolumbus.data.image

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.image.ImageScaler
import javax.inject.Inject

class AndroidImageScaler @Inject constructor(
    @Dispatcher(KolumbusDispatcher.Default) private val defaultDispatcher: CoroutineDispatcher
): ImageScaler<Bitmap> {
    override suspend fun scaleImage(image: Bitmap, imageSize: Int): Bitmap {
        return withContext(defaultDispatcher) {
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