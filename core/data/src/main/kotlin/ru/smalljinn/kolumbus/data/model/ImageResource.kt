package ru.smalljinn.kolumbus.data.model

import android.net.Uri
import ru.smalljinn.database.model.ImageEntity
import ru.smalljinn.model.data.Image

fun Image.asEntity(placeId: Long) = ImageEntity(id = id, uri = url, placeId = placeId)
fun List<Image>.asEntities(placeId: Long) = this.map { image -> image.asEntity(placeId) }
fun List<Uri>.asImageEntities(placeId: Long) =
    this.map { uri -> ImageEntity(id = 0, uri.toString(), placeId = placeId) }