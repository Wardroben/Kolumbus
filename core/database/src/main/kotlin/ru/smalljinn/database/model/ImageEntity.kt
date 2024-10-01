package ru.smalljinn.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uri: String,
    @ColumnInfo(name = "place_id")
    val placeId: Long
)
