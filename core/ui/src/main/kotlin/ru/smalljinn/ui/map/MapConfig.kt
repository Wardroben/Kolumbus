package ru.smalljinn.ui.map

import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Immutable
import ru.smalljinn.model.data.Position

@Immutable
data class MapConfig(
    val userPosition: Position,
    val usePreciseLocation: Boolean,
    val isGpsRequestDenied: Boolean,
    val hasAtLeastOneLocationPermission: Boolean,
    val followUserPositionAtStart: Boolean,
    val onUserPositionUpdated: (Position) -> Unit,
    val onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    val showNoLocationPermissionsRationale: () -> Unit,
)
