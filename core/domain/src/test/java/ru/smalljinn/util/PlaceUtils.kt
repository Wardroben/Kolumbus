package ru.smalljinn.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position

internal class PlaceUtils {
    companion object {
        fun getTestPlace(
            id: Long = 0L,
            title: String = "fooTitle",
            description: String = "fooDescription",
            position: Position = Position(12.3, 1.23),
            favorite: Boolean = false,
            creationDate: Instant = Clock.System.now(),
            headerImageId: Long? = null,
            images: List<Image> = emptyList()
        ): Place =
            Place(id, title, description, position, creationDate, headerImageId, favorite, images)

        fun getTestPlaces(count: Int) =
            List(count) { index ->
                getTestPlace(
                    id = index.toLong(),
                    title = "fooTitle$index",
                    description = "fooDescription$index"
                )
            }
    }
}