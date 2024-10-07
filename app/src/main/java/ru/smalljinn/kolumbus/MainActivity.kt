package ru.smalljinn.kolumbus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import ru.smalljinn.kolumbus.navigation.KolumbusNavHost
import ru.smalljinn.kolumbus.navigation.TopLevelDestination
import ru.smalljinn.kolumbus.ui.rememberKolumbusAppState
import ru.smalljinn.kolumbus.ui.theme.KolumbusTheme
import ru.smalljinn.ui.KolumbusTopAppBar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberKolumbusAppState()
            KolumbusTheme {
                val destination = appState.currentTopLevelDestination
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    floatingActionButton = {
                        if (destination != null) {
                            FloatingActionButton(onClick = {
                                //TODO make creation logic with navigation to empty place
                                mainViewModel.createNewPlace()
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Start creating new place"
                                )
                            }
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal
                                )
                            )
                    ) {
                        var shouldShowTopAppBar = false
                        if (destination != null) {
                            shouldShowTopAppBar = true
                            KolumbusTopAppBar(
                                titleRes = destination.titleTextId,
                                navigationIcon = destination.navigationIcon,
                                navigationContentDescription = stringResource(destination.navigationContentDescriptionId),
                                actionIcon = Icons.Default.Settings,
                                actionContentDescription = stringResource(R.string.app_open_settings_action_content_description),
                                onNavigateClick = {
                                    when (destination) {
                                        TopLevelDestination.PLACES -> Unit
                                        //TODO make sure if can navigate up
                                        else -> appState.navController.navigateUp()
                                    }
                                },
                                onActionClick = {
                                    //TODO open settings or show settings dialog
                                }
                            )

                        }
                        Box(
                            modifier = Modifier.consumeWindowInsets(
                                if (shouldShowTopAppBar) {
                                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                                } else {
                                    WindowInsets(0, 0, 0, 0)
                                },
                            ),
                        ) {
                            KolumbusNavHost(appState = appState)
                        }
                    }
                }
            }
        }
    }
}