package ru.smalljinn.ui

import android.annotation.SuppressLint
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position

private const val MAP_POSITION_ANIMATION_DURATION = 500
private const val MAP_ZOOM = 16.5f
private const val MIN_MAP_ZOOM_PLACE = 11f
private const val MIN_MAP_ZOOM_GLOBAL = 9f

@Immutable
data class MapConfig(
    val userPosition: Position,
    val usePreciseLocation: Boolean,
    val isGpsRequestDenied: Boolean,
    val hasAtLeastOneLocationPermission: Boolean,
    val onUserPositionUpdated: (Position) -> Unit,
    val onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    val showNoLocationPermissionsRationale: () -> Unit,
)

sealed class MapState {
    data class PlaceMap(
        val mapConfig: MapConfig,
        val placePosition: Position,
        val onPlacePositionUpdated: (Position) -> Unit,
        val canChangePlacePosition: Boolean
    ) : MapState()

    data class GlobalMap(val mapConfig: MapConfig, val places: List<Place>) : MapState()
}

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

@SuppressLint("MissingPermission")
@Composable
fun PlaceKolumbusMap(
    placePosition: Position,
    onPlacePositionUpdated: (Position) -> Unit,
    canChangePlacePosition: Boolean,
    mapConfig: MapConfig,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var zoom by rememberSaveable { mutableFloatStateOf(MAP_ZOOM) }
    val cameraPositionState = rememberCameraPositionState()
    var followUserPosition by rememberSaveable { mutableStateOf(false) }

    fun animateCamera(position: LatLng) {
        coroutineScope.launch {
            cameraPositionState.animate(
                getCameraUpdate(position, zoom),
                MAP_POSITION_ANIMATION_DURATION
            )
        }
    }

    //updates place position when camera stop move and it move was from user
    LaunchedEffect(cameraPositionState.isMoving) {
        //When camera stop moving change place position
        if (!cameraPositionState.isMoving && canChangePlacePosition) {
            onPlacePositionUpdated(cameraPositionState.position.target.toPosition())
            //when user moves camera following disables
        } else if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE
            && cameraPositionState.isMoving
        ) {
            followUserPosition = false
            zoom = cameraPositionState.position.zoom
        }
    }

    //Effect to track user position when it enabled
    LaunchedEffect(
        followUserPosition,
        mapConfig.userPosition,
        placePosition,
        canChangePlacePosition
    ) {
        if (canChangePlacePosition && followUserPosition && mapConfig.userPosition.isCorrect) {
            //animates camera to new user position
            animateCamera(mapConfig.userPosition.toLatLng())
        } else if (!canChangePlacePosition && placePosition.isCorrect) {
            //animates camera to new place position (used for moving camera when map loaded and when
            //user cancels editing of place to return previous position
            animateCamera(placePosition.toLatLng())
        }
    }
    val mapProperties =
        remember(mapConfig.hasAtLeastOneLocationPermission, canChangePlacePosition) {
            MapProperties(
                minZoomPreference = MIN_MAP_ZOOM_PLACE,
                isMyLocationEnabled = mapConfig.hasAtLeastOneLocationPermission && canChangePlacePosition
            )
        }
    val mapUiSettings = remember(canChangePlacePosition) {
        MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = canChangePlacePosition,
            scrollGesturesEnabled = canChangePlacePosition,
            myLocationButtonEnabled = false
        )
    }

    GoogleMapWithActions(
        mapProperties = mapProperties,
        mapUiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        shouldReceiveUserPosition = mapConfig.hasAtLeastOneLocationPermission && canChangePlacePosition,
        usePreciseLocation = mapConfig.usePreciseLocation,
        isGpsRequestDenied = mapConfig.isGpsRequestDenied,
        onGpsUnavailableResolvable = mapConfig.onGpsUnavailableResolvable,
        onUserPositionUpdated = mapConfig.onUserPositionUpdated,
        mapActionsVisible = canChangePlacePosition,
        isUserPositionFollowing = followUserPosition,
        canStartFollowUserPosition = mapConfig.hasAtLeastOneLocationPermission,
        showNoLocationPermissionsRationale = mapConfig.showNoLocationPermissionsRationale,
        showEditPlaceMarker = canChangePlacePosition,
        onFollowUserPositionChanged = { followUserPosition = it },
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) {
        //show marker in view mode else hide it
        if (!canChangePlacePosition)
            Marker(state = remember(placePosition) { MarkerState(position = placePosition.toLatLng()) })
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun GoogleMapWithActions(
    mapProperties: MapProperties,
    mapUiSettings: MapUiSettings,
    cameraPositionState: CameraPositionState,
    shouldReceiveUserPosition: Boolean,
    usePreciseLocation: Boolean,
    isGpsRequestDenied: Boolean,
    onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    onUserPositionUpdated: (Position) -> Unit,
    mapActionsVisible: Boolean,
    isUserPositionFollowing: Boolean,
    canStartFollowUserPosition: Boolean,
    showNoLocationPermissionsRationale: () -> Unit,
    onFollowUserPositionChanged: (Boolean) -> Unit,
    showEditPlaceMarker: Boolean = false,
    modifier: Modifier = Modifier,
    googleMapContent: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
            modifier = Modifier.matchParentSize()
        ) {
            googleMapContent()
        }
        if (showEditPlaceMarker) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 36.dp)
                    .size(48.dp)
            )
        }
        AnimatedVisibility(
            visible = mapActionsVisible,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FollowPositionButton(
                followingUserPosition = isUserPositionFollowing,
                hasLocationPermission = canStartFollowUserPosition,
                showNoLocationPermissionsRationale = showNoLocationPermissionsRationale
            ) { shouldFollow -> onFollowUserPositionChanged(shouldFollow) }
        }
    }

    if (shouldReceiveUserPosition) {
        PositionEffect(
            usePreciseLocation = usePreciseLocation,
            isGpsRequestDenied = isGpsRequestDenied,
            onGpsUnavailableResolvable = onGpsUnavailableResolvable
        ) { onUserPositionUpdated(it) }
    }
}

private fun getCameraUpdate(position: LatLng, zoom: Float): CameraUpdate =
    CameraUpdateFactory.newCameraPosition(
        CameraPosition.fromLatLngZoom(position, zoom)
    )