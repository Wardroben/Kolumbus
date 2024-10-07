package ru.smalljinn.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.smalljinn.edit.EditScreen

@Serializable
data class EditPlaceRoute(val placeId: Long?)

fun NavController.navigateToEdit(placeId: Long? = null, navOptions: NavOptionsBuilder.() -> Unit = {}) =
    navigate(route = EditPlaceRoute(placeId)) {
        navOptions()
    }

fun NavGraphBuilder.editScreen(showBackButton: Boolean, onBackClick: () -> Unit) {
    composable<EditPlaceRoute> {
        EditScreen(showBackButton = showBackButton, onBackClick = onBackClick)
    }
}