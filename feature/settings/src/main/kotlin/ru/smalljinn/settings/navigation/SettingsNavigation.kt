package ru.smalljinn.settings.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavHostController.navigateToSettings() {
    navigate(SettingsRoute)
}

fun NavGraphBuilder.settingsScreen() {
    composable<SettingsRoute> {
        TestSettingsScreen()
    }
}

@Composable
fun TestSettingsScreen(modifier: Modifier = Modifier) {
    Text("You are at settings", modifier = modifier)
}