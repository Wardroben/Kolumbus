package ru.smalljinn.kolumbus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.smalljinn.kolumbus.navigation.KolumbusNavHost
import ru.smalljinn.kolumbus.ui.rememberKolumbusAppState
import ru.smalljinn.kolumbus.ui.theme.KolumbusTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val importViewModel: RootComponent by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        importPlaces(intent, onImport = { uri -> importViewModel.importPlaces(uri) })

        enableEdgeToEdge()
        setContent {
            val appState = rememberKolumbusAppState()
            val scope = rememberCoroutineScope()
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
                        ObserveAsEvents(flow = importViewModel.uiEvent) { event ->
                            val text = when(event) {
                                is ImportUiEvent.Error -> event.message
                                is ImportUiEvent.Imported -> event.message
                            }
                            scope.launch {
                                appState.snackbarHostState.showSnackbar(message = text)
                            }
                            //Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                        }
                        //Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                        KolumbusNavHost(
                            appState = appState,
                            onShowMessage = { message: String ->
                                appState.coroutineScope.launch {
                                    appState.snackbarHostState.showSnackbar(
                                        message = message
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun importPlaces(intent: Intent?, onImport: (Uri) -> Unit) {
        val importType = intent?.type == "application/octet-stream"

        if (importType && intent != null) {
            when (intent.action) {
                Intent.ACTION_SEND,
                Intent.ACTION_VIEW -> {
                    val backupFileUri = intent.data
                    backupFileUri?.let { onImport(it) } ?: return
                }
            }
        }

    }
}

@Composable
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}