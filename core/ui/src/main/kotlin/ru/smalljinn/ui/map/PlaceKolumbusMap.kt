package ru.smalljinn.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import ru.smalljinn.model.data.Position

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
    var followUserPosition by rememberSaveable { mutableStateOf(mapConfig.followUserPositionAtStart) }

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
    var satelliteMode by rememberSaveable { mutableStateOf(false) }
    val mapType =
        remember(satelliteMode) { if (satelliteMode) MapType.SATELLITE else MapType.NORMAL }
    val mapProperties =
        remember(mapConfig.hasAtLeastOneLocationPermission, canChangePlacePosition, mapType) {
            MapProperties(
                minZoomPreference = MIN_MAP_ZOOM_PLACE,
                isMyLocationEnabled = mapConfig.hasAtLeastOneLocationPermission && canChangePlacePosition,
                mapType = mapType
            )
        }
    val mapUiSettings = remember(canChangePlacePosition) {
        MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = canChangePlacePosition,
            scrollGesturesEnabled = canChangePlacePosition,
            myLocationButtonEnabled = false,
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
        onLayerChangeClicked = { satelliteMode = !satelliteMode },
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) {
        //show marker in view mode else hide it
        if (!canChangePlacePosition)
            Marker(state = remember(placePosition) { MarkerState(position = placePosition.toLatLng()) })
    }
}