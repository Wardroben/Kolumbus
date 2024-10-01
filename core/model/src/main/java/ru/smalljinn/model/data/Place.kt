package ru.smalljinn.model.data

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
)