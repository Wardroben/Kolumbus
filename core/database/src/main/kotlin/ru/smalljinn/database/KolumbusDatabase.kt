package ru.smalljinn.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.dao.SearchPlaceDao
import ru.smalljinn.database.model.ImageEntity
import ru.smalljinn.database.model.PlaceEntity
import ru.smalljinn.database.util.InstantConverter
import ru.smalljinn.database.util.PositionConverter
import ru.smalljinn.database.util.UriConverter

@Database(
    entities = [
        PlaceEntity::class,
        ImageEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class,
    PositionConverter::class,
    UriConverter::class
)
internal abstract class KolumbusDatabase: RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun imageDao(): ImageDao
    abstract fun searchPlacesDao(): SearchPlaceDao
}