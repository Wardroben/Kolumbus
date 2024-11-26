package ru.smalljinn.ui.map

import android.annotation.SuppressLint
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import ru.smalljinn.model.data.Position

@SuppressLint("MissingPermission")
@Composable
internal fun GoogleMapWithActions(
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
    onLayerChangeClicked: () -> Unit,
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
                .animateContentSize(),
        ) {
            MapLayerButton { onLayerChangeClicked() }
            AnimatedVisibility(
                visible = mapActionsVisible,
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
    }

    if (shouldReceiveUserPosition) {
        PositionEffect(
            usePreciseLocation = usePreciseLocation,
            isGpsRequestDenied = isGpsRequestDenied,
            onGpsUnavailableResolvable = onGpsUnavailableResolvable
        ) { onUserPositionUpdated(it) }
    }
}