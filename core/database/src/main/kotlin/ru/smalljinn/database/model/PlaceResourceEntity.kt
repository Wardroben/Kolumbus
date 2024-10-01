package ru.smalljinn.database.model

import ru.smalljinn.model.data.Place

fun PlaceWithImages.asModel() = with(placeEntity) {
    Place(
        id = id,
        title = title,
        description = description,
        position = position,
        creationDate = creationDate,
        favorite = favorite,
        images = imageEntities.asModels(),
        headerImageId = headerImageId
    )
}

fun List<PlaceWithImages>.asModels() = this.map { placeWithImages -> placeWithImages.asModel() }