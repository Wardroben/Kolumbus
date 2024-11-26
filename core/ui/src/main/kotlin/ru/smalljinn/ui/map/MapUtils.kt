package ru.smalljinn.ui.map

import android.location.Location
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import ru.smalljinn.model.data.Position

internal fun getCameraUpdate(position: LatLng, zoom: Float): CameraUpdate =
    CameraUpdateFactory.newCameraPosition(
        CameraPosition.fromLatLngZoom(position, zoom)
    )

internal fun Position.toLatLng() = LatLng(latitude, longitude)
internal fun LatLng.toPosition() = Position(latitude, longitude)
internal fun Location.toPosition() = Position(latitude, longitude)

internal const val MAP_POSITION_ANIMATION_DURATION = 500
internal const val MAP_ZOOM = 16.5f
internal const val MIN_MAP_ZOOM_PLACE = 11f
internal const val MIN_MAP_ZOOM_GLOBAL = 9f