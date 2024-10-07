package ru.smalljinn.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.smalljinn.model.data.Position

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "place_id") val placeId: Long,
    val title: String,
    val description: String,
    val position: Position,
    @ColumnInfo(name = "creation_date") val creationDate: Instant,
    @ColumnInfo(name = "header_image_id") val headerImageId: Long?,
    val favorite: Boolean
)
