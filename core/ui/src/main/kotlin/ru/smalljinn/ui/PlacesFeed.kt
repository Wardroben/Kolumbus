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
        val useCompactMode: Boolean,
        val isDataSyncing: Boolean,
        val showOnlyFavorite: Boolean
    ) : PlacesUiState {
        val isEmpty: Boolean
            get() = places.isEmpty()
    }

    data object Empty : PlacesUiState
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