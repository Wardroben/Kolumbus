package ru.smalljinn.place.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.smalljinn.place.PlaceScreen

@Serializable
data class PlaceRoute(val id: Long, val isCreating: Boolean = false)

fun NavController.navigateToPlace(
    placeId: Long,
    isCreating: Boolean = false,
    navOptions: NavOptionsBuilder.() -> Unit
) {
    navigate(route = PlaceRoute(placeId,isCreating)) {
        navOptions()
    }
}

fun NavGraphBuilder.placeScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onPlaceDelete: (placeId: Long, title: String) -> Unit,
    onShowMessage: (Int) -> Unit,
) {
    composable<PlaceRoute> {
        PlaceScreen(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            onPlaceDelete = onPlaceDelete,
            onShowMessage = onShowMessage
        )
    }
}