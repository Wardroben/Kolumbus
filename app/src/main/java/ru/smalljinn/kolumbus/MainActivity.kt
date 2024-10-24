package ru.smalljinn.kolumbus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.navigation.KolumbusNavHost
import ru.smalljinn.kolumbus.ui.rememberKolumbusAppState
import ru.smalljinn.kolumbus.ui.theme.KolumbusTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberKolumbusAppState()
            val context = LocalContext.current
            KolumbusTheme {
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(appState.snackbarHostState) }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                            )
                    ) {
                        //Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                        KolumbusNavHost(
                            appState = appState,
                            onShowMessage = { messageId: Int ->
                                appState.coroutineScope.launch {
                                    appState.snackbarHostState.showSnackbar(
                                        message = context.getString(messageId)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}