package ru.smalljinn.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.ui.Modifier
import ru.smalljinn.model.data.Place

sealed interface PlacesUiState {
    data object Loading : PlacesUiState
    data class Success(
        val selectedPlaceId: Long?,
        val places: List<Place>,
        val useCompactMode: Boolean
    ) : PlacesUiState

    data object Empty : PlacesUiState
}

fun LazyStaggeredGridScope.placesFeed(
    placesState: PlacesUiState,
    onPlaceClicked: (Long) -> Unit,
    onPlaceFavoriteChanged: (Place, Boolean) -> Unit,
) {
    when (placesState) {
        PlacesUiState.Empty -> Unit
        PlacesUiState.Loading -> Unit
        is PlacesUiState.Success -> {
            items(
                items = placesState.places,
                key = { it.id },
                contentType = { "placesFeedItem" }) { place ->
                PlaceCard(
                    place = place,
                    onClick = { onPlaceClicked(place.id) },
                    onFavorite = { onPlaceFavoriteChanged(place, !place.favorite) },
                    compactStyle = placesState.useCompactMode,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

fun LazyStaggeredGridScope.placesFeed(
    places: List<Place>,
    onPlaceClicked: (Long) -> Unit,
    selectedPlaceId: Long?,
    favoritePlace: (Place, Boolean) -> Unit,
    highlightSelectedPlace: Boolean,
    useCompactMode: Boolean
) {
    items(items = places, key = { it.id }) { place ->
        PlaceCard(
            onClick = { onPlaceClicked(place.id) },
            onFavorite = { favoritePlace(place, !place.favorite) },
            place = place,
            isSelected = highlightSelectedPlace && selectedPlaceId == place.id,
            compactStyle = useCompactMode,
            modifier = Modifier.animateItem()
        )
    }
    item {
        Spacer(
            Modifier.windowInsetsBottomHeight(
                WindowInsets.systemBars
            )
        )
    }
}