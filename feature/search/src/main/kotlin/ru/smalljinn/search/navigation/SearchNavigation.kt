package ru.smalljinn.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.smalljinn.search.SearchScreen

@Serializable
data object SearchRoute

fun NavController.navigateToSearch() {
    navigate(SearchRoute)
}

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onPlaceClicked: (Long) -> Unit
) {
    composable<SearchRoute> {
        SearchScreen(onBackClick = onBackClick, onPlaceClicked = onPlaceClicked)
    }
}