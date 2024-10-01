package ru.smalljinn.database.model

import androidx.room.Embedded
import androidx.room.Relation


data class PlaceWithImages(
    @Embedded val placeEntity: PlaceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val imageEntities: List<ImageEntity>
)
