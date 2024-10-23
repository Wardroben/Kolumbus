package ru.smalljinn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.smalljinn.model.data.Place

@Composable
fun PlacesTabContent(
    places: List<Place>,
    selectedPlaceId: Long? = null,
    useCompactMode: Boolean,
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
        placesFeed(
            places = places,
            onPlaceClicked = onPlaceClicked,
            selectedPlaceId = selectedPlaceId,
            favoritePlace = favoritePlace,
            highlightSelectedPlace = highlightSelectedPlace,
            useCompactMode = useCompactMode
        )
    }
}