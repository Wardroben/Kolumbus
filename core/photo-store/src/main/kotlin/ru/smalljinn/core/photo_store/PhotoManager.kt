package ru.smalljinn.core.photo_store

import android.net.Uri
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result

interface PhotoManager {
    fun savePhotosToDevice(uris: List<Uri>): Result<List<Uri>, PhotoError>
    fun deletePhotoFromDevice(url: String): Result<Unit, PhotoError>
}