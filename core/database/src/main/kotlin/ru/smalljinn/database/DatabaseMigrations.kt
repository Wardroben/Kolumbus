package ru.smalljinn.database

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

object DatabaseMigrations {
    @DeleteColumn(
        tableName = "places_fts",
        columnName = "placeId"
    )
    class Schema2to3 : AutoMigrationSpec
}