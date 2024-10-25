package ru.smalljinn.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "places_fts")
@Fts4(
    //it is needed for automatic changes in places_fts when PlaceEntity changes
    contentEntity = PlaceEntity::class
)
data class PlaceFtsEntity(
    //placeId removed because default rowid used.
    /*@ColumnInfo(name = "placeId") val placeId: String,*/
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String
)

fun PlaceEntity.toPlaceFts() = PlaceFtsEntity(
    //placeId = id.toString(),
    title = title,
    description = description
)