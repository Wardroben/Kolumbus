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
import ru.smalljinn.place.PlaceDetailPlaceholder
import ru.smalljinn.place.navigation.PlaceRoute
import ru.smalljinn.place.navigation.navigateToPlace
import ru.smalljinn.place.navigation.placeScreen
import ru.smalljinn.places.PlacesRoute
import ru.smalljinn.places.navigation.PlacesRoute
import java.util.UUID

@Serializable
internal object PlacePlaceholderRoute

// TODO: Remove @Keep when https://issuetracker.google.com/353898971 is fixed
@Keep
@Serializable
internal object DetailPaneNavHostRoute

fun NavGraphBuilder.placesListDetailScreen() {
    composable<PlacesRoute> {
        PlacesListDetailScreen()
    }
}

@Composable
internal fun PlacesListDetailScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    viewModel: Places2PaneViewModel = hiltViewModel()
) {
    val selectedPlaceId by viewModel.selectedPlaceId.collectAsStateWithLifecycle()
    PlacesListDetailScreen(
        windowAdaptiveInfo = windowAdaptiveInfo,
        selectedPlaceId = selectedPlaceId,
        onPlaceClick = { viewModel.selectPlace(it) },
        placeDeleted = { viewModel.unselectPlace() }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PlacesListDetailScreen(
    onPlaceClick: (Long) -> Unit,
    selectedPlaceId: Long?,
    placeDeleted: () -> Unit,
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
        val route = selectedPlaceId?.let { PlaceRoute(id = it) } ?: PlacePlaceholderRoute
        mutableStateOf(route)
    }
    var nestedNavKey by rememberSaveable(stateSaver = Saver({ it.toString() }, UUID::fromString)) {
        mutableStateOf(UUID.randomUUID())
    }
    val nestedNavController = key(nestedNavKey) { rememberNavController() }

    fun onPlaceClickShowDetailPane(placeId: Long?) {
        if (placeId != null) onPlaceClick(placeId)
        if (listDetailNavigator.isDetailPaneVisible()) {
            nestedNavController.navigateToPlace(placeId) {
                popUpTo<DetailPaneNavHostRoute>()
            }
        } else {
            nestedNavHostStartRoute = PlaceRoute(id = placeId)
            nestedNavKey = UUID.randomUUID()
        }
        listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
    }

    fun onPlaceDeleted() {
        placeDeleted()
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
                            //TODO make creation logic with navigation to empty place
                            onPlaceClickShowDetailPane(placeId = null)
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
                        highlightSelectedPlace = listDetailNavigator.isDetailPaneVisible(),
                        modifier = Modifier.padding(padding)
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
                            onBackClick = listDetailNavigator::navigateBack,
                            showBackButton = !listDetailNavigator.isListPaneVisible(),
                            onPlaceDeleted = { onPlaceDeleted() }
                        )
                        composable<PlacePlaceholderRoute> { PlaceDetailPlaceholder() }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded