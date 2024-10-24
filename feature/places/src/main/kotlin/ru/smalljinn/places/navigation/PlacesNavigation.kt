package ru.smalljinn.places.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data class PlacesRoute(val initialPlaceId: Long? = null)

fun NavController.navigateToPlaces(
    initialPlaceId: Long? = null,
    navOptions: NavOptions? = null
) {
    navigate(route = PlacesRoute(initialPlaceId), navOptions)
}
