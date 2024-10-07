package ru.smalljinn.database.model

import ru.smalljinn.model.data.Image

fun ImageEntity.asModel() = Image(
    id = imageId,
    url = uri
)

fun List<ImageEntity>.asModels() = this.map { entity ->
    entity.asModel()
}