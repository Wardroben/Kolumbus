package ru.smalljinn.ui.map

import android.Manifest
import android.content.IntentSender
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
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
import ru.smalljinn.model.data.Position

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