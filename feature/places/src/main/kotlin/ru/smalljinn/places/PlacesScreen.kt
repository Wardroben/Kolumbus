package ru.smalljinn.places

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.smalljinn.model.data.Place
import ru.smalljinn.ui.EmptyPlacesContent
import ru.smalljinn.ui.LoadingContent
import ru.smalljinn.ui.PlacesTabContent
import ru.smalljinn.ui.PlacesUiState


@Composable
fun PlacesRoute(
    modifier: Modifier = Modifier,
    onPlaceClicked: (Long) -> Unit,
    highlightSelectedPlace: Boolean = false,
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
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
        onSearchClicked = onSearchClicked,
        onSettingsClicked = onSettingsClicked,
        modifier = modifier
    )
}

@Composable
fun PlacesScreen(
    uiState: PlacesUiState,
    onPlaceClick: (Long) -> Unit,
    highlightSelectedPlace: Boolean,
    favoritePlace: (Place, Boolean) -> Unit,
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        PlacesUiState.Empty -> {
            EmptyPlacesContent(modifier)
        }

        PlacesUiState.Loading -> {
            val loadingContentDescription = stringResource(R.string.loading_places_description)
            LoadingContent(loadingContentDescription, modifier)
        }

        is PlacesUiState.Success -> {
            Column(modifier) {
                PlacesToolbar(
                    onSettingsClicked = onSettingsClicked,
                    onSearchClicked = onSearchClicked
                )
                PlacesTabContent(
                    places = uiState.places,
                    selectedPlaceId = uiState.selectedPlaceId,
                    useCompactMode = uiState.useCompactMode,
                    onPlaceClicked = onPlaceClick,
                    favoritePlace = favoritePlace,
                    highlightSelectedPlace = highlightSelectedPlace
                )
            }
        }
    }
}

@Composable
private fun PlacesToolbar(
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, top = 8.dp)
    ) {
        IconButton(onSearchClicked) {
            Icon(Icons.Default.Search, contentDescription = "Search places")
        }
        Text("Kolumbus", style = MaterialTheme.typography.titleLarge)
        IconButton(onSettingsClicked) {
            Icon(Icons.Default.Settings, contentDescription = "Open settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlacesToolbarPreview() {
    PlacesToolbar({},{})
}