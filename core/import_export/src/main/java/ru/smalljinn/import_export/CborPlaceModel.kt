package ru.smalljinn.import_export

import android.net.Uri
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position

@Serializable
data class CborPlaceModel(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val favorite: Boolean,
    val headerImageId: Long?,
    val timestamp: Long,
    val images: List<ByteArray>
)

@Serializable
data class BackupData(
    val cborPlaces: List<CborPlaceModel>
)

fun CborPlaceModel.toModel(imageUris: List<Uri>) = Place(
    id = 0,
    title = title,
    description = description,
    favorite = favorite,
    position = Position(latitude, longitude),
    headerImageId = headerImageId,
    creationDate = Instant.fromEpochMilliseconds(timestamp),
    images = imageUris.map { uri -> Image(id = 0, url = uri.toString()) }
)

internal fun Place.toCbor(imagesByte: List<ByteArray>) = CborPlaceModel(
    title = title,
    description = description,
    latitude = position.latitude,
    longitude = position.longitude,
    favorite = favorite,
    headerImageId = headerImageId,
    timestamp = creationDate.toEpochMilliseconds(),
    images = imagesByte
)