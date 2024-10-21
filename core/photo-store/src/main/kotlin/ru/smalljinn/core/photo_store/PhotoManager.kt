package ru.smalljinn.core.photo_store

import android.net.Uri
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result

interface PhotoManager {
    suspend fun savePhotosToDevice(uris: List<Uri>): Result<List<Uri>, PhotoError>
    suspend fun deletePhotoFromDevice(uri: Uri): Result<Unit, PhotoError>
    fun getUriForTakePhoto(): Uri
}