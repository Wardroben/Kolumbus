package ru.smalljinn.database.util

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

internal class UriConverter {
    @TypeConverter
    fun uriToUrl(uri: Uri?): String? =
        uri?.toString()

    @TypeConverter
    fun urlToUri(value: String?): Uri? =
        value?.toUri()
}