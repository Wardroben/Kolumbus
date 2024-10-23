package ru.smalljinn.places

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.smalljinn.model.data.Place
import ru.smalljinn.ui.PlaceCard
import ru.smalljinn.ui.PlacesUiState

@Composable
fun PlacesTabContent(
    successUiState: PlacesUiState.Success,
    onPlaceClicked: (Long) -> Unit,
    highlightSelectedPlace: Boolean,
    favoritePlace: (Place, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        modifier = modifier,
    ) {
        items(items = successUiState.places, key = { it.id }) { place ->
            PlaceCard(
                onClick = { onPlaceClicked(place.id) },
                onFavorite = { favoritePlace(place, !place.favorite) },
                place = place,
                isSelected = highlightSelectedPlace && successUiState.selectedPlaceId == place.id,
                compactStyle = successUiState.useCompactMode,
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
}