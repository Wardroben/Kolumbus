package ru.smalljinn.model.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Place(
    val id: Long,
    val title: String,
    val description: String,
    val position: Position,
    val creationDate: Instant,
    val headerImageId: Long?,
    val favorite: Boolean,
    val images: List<Image>
) {
    val isPlaceInvalid: Boolean
        get() = position == Position.initialPosition()
    companion object {
        fun getInitPlace() = Place(
            id = -1L,
            title = "",
            description = "",
            position = Position.initialPosition(),
            creationDate = Clock.System.now(),
            headerImageId = null,
            favorite = false,
            images = emptyList()
        )
        const val CREATION_ID = -1L
    }
}