package ru.smalljinn.database.util

import androidx.room.TypeConverter
import ru.smalljinn.model.data.Position

const val POSITION_SPLITTER = ','

class PositionConverter {
    @TypeConverter
    fun positionToString(position: Position?): String? =
        "${position?.latitude}$POSITION_SPLITTER${position?.longitude}"

    @TypeConverter
    fun stringToPosition(value: String?): Position? {
        if (value.isNullOrBlank()) return null
        val (longitude, latitude) = value.split(POSITION_SPLITTER)
            .map { coordinate -> coordinate.toDouble() }
        return Position(longitude, latitude)
    }

}