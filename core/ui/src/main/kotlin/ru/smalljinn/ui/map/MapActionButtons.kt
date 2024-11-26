package ru.smalljinn.ui.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.smalljinn.ui.R

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

@Composable
fun MapLayerButton(onChangeLayer: () -> Unit) {
    FilledIconButton(
        onClick = onChangeLayer,
        colors = IconButtonDefaults.filledIconButtonColors()
            .copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_layers_24),
            contentDescription = stringResource(R.string.change_map_layer_cd)
        )
    }
}

@Composable
private fun MapButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ElevatedButton(
        modifier = modifier.minimumInteractiveComponentSize(),
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.elevatedButtonColors()
            .copy(contentColor = MaterialTheme.colorScheme.onSurface),
        contentPadding = PaddingValues(0.dp)
    ) {
        content()
    }
}