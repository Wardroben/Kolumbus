package ru.smalljinn.kolumbus.data.model

import ru.smalljinn.database.model.PlaceEntity
import ru.smalljinn.model.data.Place

fun Place.asEntity() = PlaceEntity(
    id = id,
    title = title,
    description = description,
    position = position,
    creationDate = creationDate,
    headerImageId = headerImageId,
    favorite = favorite
)
