package ru.smalljinn.database.model

import androidx.room.Embedded
import androidx.room.Relation


data class PlaceWithImages(
    @Embedded val placeEntity: PlaceEntity,
    @Relation(
        parentColumn = "place_id",
        entityColumn = "place_id"
    )
    val imageEntities: List<ImageEntity>
)
