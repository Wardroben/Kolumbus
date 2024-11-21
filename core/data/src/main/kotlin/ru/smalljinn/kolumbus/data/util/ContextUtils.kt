package ru.smalljinn.kolumbus.data.util

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.image.ImageFileProvider

internal fun Context.clearCache(dispatcher: CoroutineDispatcher) {
    CoroutineScope(dispatcher).launch {
        coroutineScope {
            runCatching {
                val imageCacheDirectory = ImageFileProvider.getTemporaryDirectory(this@clearCache)
                imageCacheDirectory.deleteRecursively()
            }
        }
    }
}