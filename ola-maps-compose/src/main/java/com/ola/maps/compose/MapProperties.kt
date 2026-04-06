package com.ola.maps.compose

import androidx.compose.runtime.Immutable
import com.ola.mapsdk.camera.MapControlSettings

/**
 * Stable map properties that are safe to apply from Compose state.
 *
 * Example:
 * ```kotlin
 * val properties = MapProperties(
 *     isMyLocationEnabled = true,
 *     moveCameraToMyLocationOnEnable = true,
 * )
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
    val moveCameraToMyLocationOnEnable: Boolean = false,
    val myLocationZoomLevel: Double = 15.0,
    val myLocationAnimationDurationMs: Int = 800,
)

/**
 * Stable UI settings for gestures and map controls.
 *
 * Example:
 * ```kotlin
 * val uiSettings = MapUiSettings.allGesturesDisabled(compassEnabled = true)
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
) {
    companion object {
        /**
         * Returns the default settings with all supported gestures enabled.
         */
        fun allGesturesEnabled(): MapUiSettings = MapUiSettings()

        /**
         * Returns a settings object with all gesture interactions disabled.
         */
        fun allGesturesDisabled(
            compassEnabled: Boolean = true,
        ): MapUiSettings = MapUiSettings(
            isRotateGesturesEnabled = false,
            isScrollGesturesEnabled = false,
            isZoomGesturesEnabled = false,
            isCompassEnabled = compassEnabled,
            isTiltGesturesEnabled = false,
            isDoubleTapGesturesEnabled = false,
        )
    }
}

internal fun MapUiSettings.toSdkSettings(): MapControlSettings =
    MapControlSettings.Builder()
        .setRotateGesturesEnabled(isRotateGesturesEnabled)
        .setScrollGesturesEnabled(isScrollGesturesEnabled)
        .setZoomGesturesEnabled(isZoomGesturesEnabled)
        .setCompassEnabled(isCompassEnabled)
        .setTiltGesturesEnabled(isTiltGesturesEnabled)
        .setDoubleTapGesturesEnabled(isDoubleTapGesturesEnabled)
        .build()
