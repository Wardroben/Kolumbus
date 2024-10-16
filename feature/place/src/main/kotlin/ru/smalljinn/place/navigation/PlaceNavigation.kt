package ru.smalljinn.place.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.smalljinn.place.PlaceScreen

@Serializable
data class PlaceRoute(val id: Long)

fun NavController.navigateToPlace(placeId: Long, navOptions: NavOptionsBuilder.() -> Unit) {
    navigate(route = PlaceRoute(placeId)) {
        navOptions()
    }
}

fun NavGraphBuilder.placeScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onPlaceDeleted: () -> Unit,
    onShowMessage: (Int) -> Unit
) {
    composable<PlaceRoute> {
        PlaceScreen(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            onPlaceDeleted = onPlaceDeleted,
            onShowMessage = onShowMessage
        )
    }
}