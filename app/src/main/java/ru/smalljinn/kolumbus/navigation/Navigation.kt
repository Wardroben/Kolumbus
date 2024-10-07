package ru.smalljinn.kolumbus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.smalljinn.kolumbus.ui.KolumbusAppState
import ru.smalljinn.kolumbus.ui.places2pane.placesListDetailScreen
import ru.smalljinn.places.navigation.PlacesRoute

@Composable
fun KolumbusNavHost(appState: KolumbusAppState,modifier: Modifier = Modifier) {
    val navController = appState.navController
    NavHost(navController = navController, startDestination = PlacesRoute(), modifier = modifier) {
        placesListDetailScreen()
    }
}
