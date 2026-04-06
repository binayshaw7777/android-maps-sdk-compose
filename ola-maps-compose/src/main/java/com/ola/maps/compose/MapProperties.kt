package com.ola.maps.compose

import androidx.compose.runtime.Immutable
import com.ola.mapsdk.camera.MapControlSettings

@Immutable
data class MapProperties(
    val isMyLocationEnabled: Boolean = false,
)

@Immutable
data class MapUiSettings(
    val isRotateGesturesEnabled: Boolean = true,
    val isScrollGesturesEnabled: Boolean = true,
    val isZoomGesturesEnabled: Boolean = true,
    val isCompassEnabled: Boolean = true,
    val isTiltGesturesEnabled: Boolean = true,
    val isDoubleTapGesturesEnabled: Boolean = true,
)

internal fun MapUiSettings.toSdkSettings(): MapControlSettings =
    MapControlSettings.Builder()
        .setRotateGesturesEnabled(isRotateGesturesEnabled)
        .setScrollGesturesEnabled(isScrollGesturesEnabled)
        .setZoomGesturesEnabled(isZoomGesturesEnabled)
        .setCompassEnabled(isCompassEnabled)
        .setTiltGesturesEnabled(isTiltGesturesEnabled)
        .setDoubleTapGesturesEnabled(isDoubleTapGesturesEnabled)
        .build()
