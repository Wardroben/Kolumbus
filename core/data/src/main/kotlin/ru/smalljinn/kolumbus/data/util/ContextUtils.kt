package ru.smalljinn.kolumbus.data.util

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal fun Context.clearCache(dispatcher: CoroutineDispatcher) {
    CoroutineScope(dispatcher).launch {
        coroutineScope {
            runCatching {
                cacheDir?.deleteRecursively()
                codeCacheDir?.deleteRecursively()
                externalCacheDir?.deleteRecursively()
                externalCacheDirs?.forEach {
                    it.deleteRecursively()
                }
            }
        }
    }
}