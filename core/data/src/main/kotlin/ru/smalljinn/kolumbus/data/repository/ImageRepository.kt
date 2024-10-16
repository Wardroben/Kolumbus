package ru.smalljinn.kolumbus.data.repository

import android.net.Uri
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.response.PhotoError
import ru.smalljinn.model.data.response.Result

interface ImageRepository {
    suspend fun insertImages(imageUris: List<Uri>, placeId: Long): Result<Unit, PhotoError>
    suspend fun deleteImage(image: Image): Result<Unit, PhotoError>
    suspend fun getPlaceImages(placeId: Long): List<Image>
}