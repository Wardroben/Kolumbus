package ru.smalljinn.kolumbus

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.Places, modifier = modifier) {
        composable<NavRoutes.Places> {
            MainScreen()
        }

        composable<NavRoutes.Settings> {
            TODO()
        }

        composable<NavRoutes.EditPlace> {
            TODO()
        }
    }
}

//TODO move nav destinations in features
sealed class NavRoutes {
    @Serializable
    data object Places: NavRoutes()
    @Serializable
    data class EditPlace(val placeId: Long): NavRoutes()
    @Serializable
    data object Settings: NavRoutes()
}