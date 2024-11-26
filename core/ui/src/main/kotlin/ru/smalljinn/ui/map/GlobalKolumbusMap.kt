package ru.smalljinn.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import ru.smalljinn.model.data.Place

@Composable
fun GlobalKolumbusMap(
    places: List<Place>,
    mapConfig: MapConfig,
    onPlaceClicked: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var zoom by rememberSaveable { mutableFloatStateOf(MAP_ZOOM) }
    val cameraPositionState = rememberCameraPositionState()
    var followUserPosition by rememberSaveable { mutableStateOf(true) }

    fun animateCamera(position: LatLng) {
        coroutineScope.launch {
            cameraPositionState.animate(
                getCameraUpdate(position, zoom),
                MAP_POSITION_ANIMATION_DURATION
            )
        }
    }

    LaunchedEffect(followUserPosition, mapConfig.userPosition) {
        if (followUserPosition && mapConfig.userPosition.isCorrect) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        mapConfig.userPosition.toLatLng(),
                        cameraPositionState.position.zoom
                    )
                )
            )
            //animateCamera(position = mapConfig.userPosition.toLatLng())
        }
    }

    val mapProperties = remember(mapConfig.hasAtLeastOneLocationPermission) {
        MapProperties(
            minZoomPreference = MIN_MAP_ZOOM_GLOBAL,
            isMyLocationEnabled = mapConfig.hasAtLeastOneLocationPermission
        )
    }
    val mapUiSettings = remember { MapUiSettings(myLocationButtonEnabled = false) }

    GoogleMapWithActions(
        mapProperties = mapProperties,
        mapUiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        shouldReceiveUserPosition = mapConfig.hasAtLeastOneLocationPermission,
        usePreciseLocation = mapConfig.usePreciseLocation,
        isGpsRequestDenied = mapConfig.isGpsRequestDenied,
        onGpsUnavailableResolvable = mapConfig.onGpsUnavailableResolvable,
        onUserPositionUpdated = mapConfig.onUserPositionUpdated,
        mapActionsVisible = true,
        isUserPositionFollowing = followUserPosition,
        canStartFollowUserPosition = mapConfig.hasAtLeastOneLocationPermission,
        showNoLocationPermissionsRationale = mapConfig.showNoLocationPermissionsRationale,
        onFollowUserPositionChanged = { followUserPosition = it },
        onLayerChangeClicked = {TODO()},
        modifier = modifier
    ) {
        places.forEach { place ->
            Marker(
                state = MarkerState(place.position.toLatLng()),
                title = place.title,
            )
        }
    }
}