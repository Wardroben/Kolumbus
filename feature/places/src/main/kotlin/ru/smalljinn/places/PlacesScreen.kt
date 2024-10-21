package ru.smalljinn.places

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.smalljinn.model.data.Place
import ru.smalljinn.ui.EmptyPlacesContent
import ru.smalljinn.ui.LoadingContent
import ru.smalljinn.ui.PlacesUiState


@Composable
fun PlacesRoute(
    modifier: Modifier = Modifier,
    onPlaceClicked: (Long) -> Unit,
    highlightSelectedPlace: Boolean = false,
    viewmodel: PlacesViewModel = hiltViewModel()
) {
    val placesState by viewmodel.placesState.collectAsStateWithLifecycle()

    PlacesScreen(
        uiState = placesState,
        onPlaceClick = { placeId ->
            viewmodel.obtainEvent(PlaceEvent.SelectPlace(placeId))
            onPlaceClicked(placeId)
        },
        favoritePlace = { place, favorite ->
            viewmodel.obtainEvent(PlaceEvent.MakeFavorite(place, favorite))
        },
        highlightSelectedPlace = highlightSelectedPlace,
        modifier = modifier
    )
}

@Composable
fun PlacesScreen(
    uiState: PlacesUiState,
    onPlaceClick: (Long) -> Unit,
    highlightSelectedPlace: Boolean,
    favoritePlace: (Place, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        when (uiState) {
            PlacesUiState.Empty -> {
                EmptyPlacesContent()
            }

            PlacesUiState.Loading -> {
                val loadingContentDescription = stringResource(R.string.loading_places_description)
                LoadingContent(loadingContentDescription)
            }

            is PlacesUiState.Success -> {
                PlacesTabContent(
                    places = uiState.places,
                    onPlaceClicked = onPlaceClick,
                    selectedPlaceId = uiState.selectedPlaceId,
                    favoritePlace = favoritePlace,
                    highlightSelectedPlace = highlightSelectedPlace
                )
            }
        }
    }
}

/*
@Composable
fun PlacesScreen(
    selectedPlaceId: Long?,
    isNoPlaces: Boolean,
    isPlacesLoading: Boolean,
    placesState: PlacesUiState,
    onPlaceClicked: (Long) -> Unit,
    onPlaceFavoriteChanged: (Place, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            modifier = modifier,
        ) {
            placesFeed(
                placesState = placesState,
                onPlaceFavoriteChanged = onPlaceFavoriteChanged,
                onPlaceClicked = onPlaceClicked
            )
        }
        AnimatedVisibility(visible = isNoPlaces) { EmptyPlacesContent() }
        AnimatedVisibility(visible = isPlacesLoading) {
            val loadingContentDescription = "Loading places..."
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    CircularProgressIndicator()
                    Text(loadingContentDescription)
                }
            }
        }
    }
}*/
