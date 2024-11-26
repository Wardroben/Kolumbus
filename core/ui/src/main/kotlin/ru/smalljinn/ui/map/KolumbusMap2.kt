package ru.smalljinn.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun KolumbusMap(mapState: MapState, modifier: Modifier = Modifier) {
    when (mapState) {
        is MapState.GlobalMap -> GlobalKolumbusMap(
            places = mapState.places,
            mapConfig = mapState.mapConfig,
            onPlaceClicked = { TODO() },
            modifier = modifier
        )

        is MapState.PlaceMap -> PlaceKolumbusMap(
            placePosition = mapState.placePosition,
            mapConfig = mapState.mapConfig,
            onPlacePositionUpdated = mapState.onPlacePositionUpdated,
            canChangePlacePosition = mapState.canChangePlacePosition,
            modifier = modifier
        )
    }
}