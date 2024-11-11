package ru.smalljinn.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import ru.smalljinn.model.data.Position

fun Position.toLatLng() = LatLng(latitude, longitude)
fun LatLng.toPosition() = Position(latitude, longitude)
fun Location.toPosition() = Position(latitude, longitude)
private const val MAP_POSITION_ANIMATION_DURATION = 500
private const val MAP_ZOOM = 16.5f


@SuppressLint("MissingPermission")
@Composable
fun KolumbusMap(
    placePosition: Position,
    userPosition: Position,
    onUserPositionUpdated: (Position) -> Unit,
    onPlacePositionUpdated: (Position) -> Unit,
    onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    hasLocationPermission: Boolean,
    usePreciseLocation: Boolean,
    followUserPositionAtStart: Boolean,
    shouldReceivePositionUpdates: Boolean,
    isGpsRequestDenied: Boolean,
    shouldShowPlaceMarker: Boolean,
    shouldShowPlacePositionEditingMarker: Boolean,
    showNoLocationPermissionsRationale: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var followUserPosition by rememberSaveable(
        followUserPositionAtStart,
        shouldReceivePositionUpdates
    ) {
        mutableStateOf(followUserPositionAtStart && shouldReceivePositionUpdates)
    }
    var zoom by remember { mutableFloatStateOf(MAP_ZOOM) }
    val cameraPositionState = rememberCameraPositionState()

    fun animateCamera(position: LatLng) {
        coroutineScope.launch {
            cameraPositionState.animate(
                getCameraUpdate(position, zoom),
                MAP_POSITION_ANIMATION_DURATION
            )
        }
    }

    //Effect to track user position when it enabled
    LaunchedEffect(
        hasLocationPermission,
        userPosition,
        placePosition,
        followUserPosition,
        shouldReceivePositionUpdates
    ) {
        //animates camera to new user position
        if (hasLocationPermission && followUserPosition && !userPosition.isInit) {
            animateCamera(userPosition.toLatLng())
            //animates camera to new place position (used for moving camera when map loaded and when
            //user cancels editing of place to return previous position
        } else if (!shouldReceivePositionUpdates && !placePosition.isInit) {
            cameraPositionState.move(getCameraUpdate(placePosition.toLatLng(), zoom))
        }
    }

    //Effect that sets new place position when camera moving by user is ended end
    //disables position tracking when camera moving started by user
    LaunchedEffect(cameraPositionState.isMoving, shouldReceivePositionUpdates) {
        if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE
            && cameraPositionState.isMoving
        ) {
            followUserPosition = false
            zoom = cameraPositionState.position.zoom
        } else if (!cameraPositionState.isMoving) {
            if (shouldReceivePositionUpdates) with(cameraPositionState.position) {
                onPlacePositionUpdated(target.toPosition())
            }
        }
    }

    if (hasLocationPermission && shouldReceivePositionUpdates) {
        PositionEffect(
            usePreciseLocation = usePreciseLocation,
            onGpsUnavailableResolvable = onGpsUnavailableResolvable,
            isGpsRequestDenied = isGpsRequestDenied
        ) { onUserPositionUpdated(it) }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                minZoomPreference = 10f,
                isMyLocationEnabled = hasLocationPermission && shouldReceivePositionUpdates
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = shouldReceivePositionUpdates,
                myLocationButtonEnabled = false
            ),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
            modifier = Modifier.matchParentSize()
        ) {
            if (shouldShowPlaceMarker && !placePosition.isInit) {
                val placeMarker = rememberMarkerState(position = placePosition.toLatLng())
                Marker(state = placeMarker)
            }
        }
        if (shouldShowPlacePositionEditingMarker) {
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
            visible = shouldReceivePositionUpdates,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FollowPositionButton(
                followingUserPosition = followUserPosition,
                hasLocationPermission = hasLocationPermission,
                showNoLocationPermissionsRationale = showNoLocationPermissionsRationale
            ) { followUserPosition = it }
        }
    }
}

private fun getCameraUpdate(position: LatLng, zoom: Float): CameraUpdate =
    CameraUpdateFactory.newCameraPosition(
        CameraPosition.fromLatLngZoom(position, zoom)
    )

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun PositionEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    usePreciseLocation: Boolean,
    isGpsRequestDenied: Boolean,
    onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    onGetPosition: (Position) -> Unit
) {
    val context = LocalContext.current
    val currentOnGetPosition by rememberUpdatedState(onGetPosition)
    val currentOnGpsDisabled by rememberUpdatedState(onGpsUnavailableResolvable)

    val settingsClient = remember { LocationServices.getSettingsClient(context) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            location.lastLocation?.let { currentOnGetPosition(it.toPosition()) }
        }
    }

    DisposableEffect(lifecycleOwner, isGpsRequestDenied) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val priority = if (usePreciseLocation) {
                        Priority.PRIORITY_HIGH_ACCURACY
                    } else {
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }

                    val locationRequest = LocationRequest.Builder(priority, 5_000L).build()

                    val settingsRequest =
                        LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                            .build()

                    val settingsTask = settingsClient.checkLocationSettings(settingsRequest)

                    settingsTask.addOnSuccessListener {
                        locationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) currentOnGetPosition(location.toPosition())
                        }
                        locationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                    settingsTask.addOnFailureListener { exception ->
                        if (!isGpsRequestDenied && exception is ResolvableApiException) {
                            try {
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(exception.resolution).build()
                                currentOnGpsDisabled(intentSenderRequest)
                            } catch (ex: IntentSender.SendIntentException) {
                                //ignore error
                            }
                        }
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    locationClient.removeLocationUpdates(locationCallback)
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            locationClient.removeLocationUpdates(locationCallback)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun FollowPositionButton(
    modifier: Modifier = Modifier,
    followingUserPosition: Boolean,
    hasLocationPermission: Boolean,
    showNoLocationPermissionsRationale: () -> Unit,
    onFollowChanged: (Boolean) -> Unit
) {
    FilledTonalIconToggleButton(
        modifier = modifier,
        checked = followingUserPosition,
        onCheckedChange = { checked ->
            if (hasLocationPermission) {
                onFollowChanged(checked)
            } else {
                showNoLocationPermissionsRationale()
            }
        },
    ) {
        AnimatedContent(hasLocationPermission, label = "TrackPositionBtn") { hasPermissions ->
            if (hasPermissions) Icon(
                painter = painterResource(R.drawable.baseline_my_location_24),
                contentDescription = stringResource(R.string.follow_my_position_cd),
                tint = if (followingUserPosition) MaterialTheme.colorScheme.primary else LocalContentColor.current
            ) else Icon(
                painter = painterResource(R.drawable.baseline_location_disabled_24),
                contentDescription = stringResource(R.string.track_location_unavailable)
            )
        }

    }
}