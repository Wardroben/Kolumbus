package ru.smalljinn.kolumbus.ui.places2pane

import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ru.smalljinn.kolumbus.R
import ru.smalljinn.model.data.Place
import ru.smalljinn.place.PlaceDetailPlaceholder
import ru.smalljinn.place.navigation.PlaceRoute
import ru.smalljinn.place.navigation.navigateToPlace
import ru.smalljinn.place.navigation.placeScreen
import ru.smalljinn.places.PlacesRoute
import ru.smalljinn.places.navigation.PlacesRoute
import ru.smalljinn.settings.SettingsDialog
import ru.smalljinn.ui.dialogs.DeleteDialog
import java.util.UUID

@Serializable
internal object PlacePlaceholderRoute

// TODO: Remove @Keep when https://issuetracker.google.com/353898971 is fixed
@Keep
@Serializable
internal object DetailPaneNavHostRoute

fun NavGraphBuilder.placesListDetailScreen(
    onShowMessage: (Int) -> Unit,
    onSearchClicked: () -> Unit,
) {
    composable<PlacesRoute> {
        PlacesListDetailScreen(
            onShowMessage = onShowMessage,
            onSearchClicked = onSearchClicked
        )
    }
}

@Composable
internal fun PlacesListDetailScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    viewModel: Places2PaneViewModel = hiltViewModel(),
    onShowMessage: (Int) -> Unit,
    onSearchClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    //TODO why if read id from state navigate to place not working, but this way is?
    val selectedPlaceId by viewModel.selectedPlaceId.collectAsStateWithLifecycle()

    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    if (showSettingsDialog) SettingsDialog(onDismiss = { showSettingsDialog = false })
    PlacesListDetailScreen(
        windowAdaptiveInfo = windowAdaptiveInfo,
        selectedPlaceId = selectedPlaceId,
        placesState = state,
        onPlaceClick = { viewModel.selectPlace(it) },
        setPlaceToDelete = { id, title -> viewModel.setToDeletePlace(id, title) },
        onDeleteDismiss = viewModel::clearPlaceToDelete,
        onPlaceDeletionConfirmed = viewModel::deletePlace,
        onSearchClicked = onSearchClicked,
        onSettingsClicked = { showSettingsDialog = true },
        onShowMessage = onShowMessage
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PlacesListDetailScreen(
    onPlaceClick: (Long) -> Unit,
    selectedPlaceId: Long?,
    placesState: Place2PaneState,
    setPlaceToDelete: (placeId: Long, title: String) -> Unit,
    onDeleteDismiss: () -> Unit,
    onPlaceDeletionConfirmed: () -> Unit,
    onShowMessage: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo),
        initialDestinationHistory = listOfNotNull(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem<Nothing>(ListDetailPaneScaffoldRole.Detail).takeIf {
                selectedPlaceId != null
            }
        )
    )

    BackHandler(listDetailNavigator.canNavigateBack()) { listDetailNavigator.navigateBack() }

    var nestedNavHostStartRoute by remember {
        val route =
            selectedPlaceId?.let { PlaceRoute(id = it) } ?: PlacePlaceholderRoute
        mutableStateOf(route)
    }
    var nestedNavKey by rememberSaveable(stateSaver = Saver({ it.toString() }, UUID::fromString)) {
        mutableStateOf(UUID.randomUUID())
    }
    val nestedNavController = key(nestedNavKey) { rememberNavController() }

    fun onPlaceClickShowDetailPane(placeId: Long, isCreating: Boolean = false) {
        onPlaceClick(placeId)
        if (listDetailNavigator.isDetailPaneVisible()) {
            nestedNavController.navigateToPlace(placeId, isCreating) {
                popUpTo<DetailPaneNavHostRoute>()
            }
        } else {
            nestedNavHostStartRoute = PlaceRoute(id = placeId, isCreating)
            nestedNavKey = UUID.randomUUID()
        }
        listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
    }

    fun onPlaceDelete() {
        onPlaceDeletionConfirmed()
        if (!listDetailNavigator.isDetailPaneVisible()) {
            listDetailNavigator.navigateBack()
        }
        listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List)
        nestedNavController.navigate(PlacePlaceholderRoute)
    }

    ListDetailPaneScaffold(
        value = listDetailNavigator.scaffoldValue,
        directive = listDetailNavigator.scaffoldDirective,
        listPane = {
            AnimatedPane {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            onPlaceClickShowDetailPane(placeId = -1L, isCreating = true)
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.start_creating_new_place)
                            )
                        }
                    }
                ) { padding ->
                    PlacesRoute(
                        onPlaceClicked = ::onPlaceClickShowDetailPane,
                        highlightSelectedPlace = listDetailNavigator.isDetailPaneVisible() && placesState.selectedPlaceId != Place.CREATION_ID,
                        modifier = Modifier.padding(padding),
                        onSettingsClicked = onSettingsClicked,
                        onSearchClicked = onSearchClicked,
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                key(nestedNavKey) {
                    NavHost(
                        navController = nestedNavController,
                        startDestination = nestedNavHostStartRoute,
                        route = DetailPaneNavHostRoute::class
                    ) {
                        placeScreen(
                            showBackButton = !listDetailNavigator.isListPaneVisible(),
                            onBackClick = listDetailNavigator::navigateBack,
                            onPlaceDelete = { id, title -> setPlaceToDelete(id, title) },
                            onShowMessage = onShowMessage
                        )
                        composable<PlacePlaceholderRoute> { PlaceDetailPlaceholder() }
                    }
                }
            }
        }
    )

    if (placesState.placeToDelete != null) DeleteDialog(
        onDismiss = onDeleteDismiss,
        confirmDeletion = { onPlaceDelete() },
        placeTitle = placesState.placeToDelete.title
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded