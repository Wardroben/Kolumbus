package ru.smalljinn.kolumbus.data.image

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.image.ImageFormat
import ru.smalljinn.domain.image.ImageGetter
import javax.inject.Inject

class AndroidImageGetter @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(KolumbusDispatcher.Default) private val defaultDispatcher: CoroutineDispatcher,
    private val imageLoader: ImageLoader
): ImageGetter<Bitmap> {
    override suspend fun getImage(uri: String): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()
    }

    override fun getImageFormat(uri: String): ImageFormat {
        val mimeType = context.contentResolver.getType(uri.toUri())
        return ImageFormat[mimeType]
    }
}