package ru.smalljinn.places.navigation

import kotlinx.serialization.Serializable

@Serializable
data class PlacesRoute(val initialPlaceId: Long? = null)
