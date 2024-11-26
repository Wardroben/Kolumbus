package ru.smalljinn.ui.map

import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position

sealed class MapState {
    data class PlaceMap(
        val mapConfig: MapConfig,
        val placePosition: Position,
        val onPlacePositionUpdated: (Position) -> Unit,
        val canChangePlacePosition: Boolean,
    ) : MapState()

    data class GlobalMap(val mapConfig: MapConfig, val places: List<Place>) : MapState()
}
