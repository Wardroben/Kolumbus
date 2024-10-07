package ru.smalljinn.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["place_id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["place_id"])
    ]
)

data class ImageEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "image_id") val imageId: Long,
    val uri: String,
    @ColumnInfo(name = "place_id") val placeId: Long
)
