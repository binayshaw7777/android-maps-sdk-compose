package com.ola.maps.compose

import androidx.compose.runtime.Immutable
import com.ola.mapsdk.camera.MapControlSettings

/**
 * Stable map properties that are safe to apply from Compose state.
 *
 * Example:
 * ```kotlin
 * val properties = MapProperties(isMyLocationEnabled = true)
 *
 * OlaMap(
 *     apiKey = BuildConfig.OLA_MAPS_API_KEY,
 *     properties = properties,
 * ) { }
 * ```
 */
@Immutable
data class MapProperties(
    val isMyLocationEnabled: Boolean = false,
)

/**
 * Stable UI settings for gestures and map controls.
 *
 * Example:
 * ```kotlin
 * val uiSettings = MapUiSettings(
 *     isZoomGesturesEnabled = true,
 *     isCompassEnabled = true,
 *     isRotateGesturesEnabled = false,
 * )
 * ```
 */
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
