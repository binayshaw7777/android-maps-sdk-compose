package com.ola.maps.compose

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaMarkerOptions
import com.ola.mapsdk.view.Marker as SdkMarker
import com.ola.mapsdk.view.OlaMap as SdkOlaMap

/**
 * Compose state holder for a marker position.
 *
 * Example:
 * ```kotlin
 * val markerState = rememberMarkerState(
 *     position = OlaLatLng(12.931423492103944, 77.61648476788898),
 * )
 * ```
 */
@Stable
class MarkerState internal constructor(
    position: OlaLatLng,
) {
    var position: OlaLatLng by mutableStateOf(position)
}

/**
 * Creates and remembers a [MarkerState].
 */
@Composable
fun rememberMarkerState(
    position: OlaLatLng,
): MarkerState = rememberSaveable(
    saver = MarkerStateSaver,
) {
    MarkerState(position = position)
}

/**
 * Adds a declarative marker to [OlaMap].
 *
 * The marker stays in sync with [state] and supports common SDK updates such as icon changes,
 * snippet updates, anchor changes, rotation, and size changes.
 *
 * Example:
 * ```kotlin
 * val olaCampus = OlaLatLng(12.931423492103944, 77.61648476788898)
 * val markerState = rememberMarkerState(position = olaCampus)
 *
 * OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
 *     Marker(
 *         state = markerState,
 *         snippet = "Ola Campus",
 *         subSnippet = "Compose marker",
 *         iconResId = R.drawable.ic_top_truck,
 *         onClick = {
 *             println("Marker clicked")
 *         },
 *     )
 * }
 * ```
 */
@Composable
@OlaMapComposable
fun Marker(
    state: MarkerState,
    snippet: String? = null,
    subSnippet: String? = null,
    @DrawableRes iconResId: Int? = null,
    iconBitmap: Bitmap? = null,
    isIconClickable: Boolean = true,
    isAnimationEnabled: Boolean = false,
    isInfoWindowDismissOnClick: Boolean = true,
    iconRotation: Float = 0f,
    iconSize: Float = 1f,
    iconAnchor: String? = null,
    iconOffset: FloatArray? = null,
    onClick: (() -> Unit)? = null,
) {
    val position = state.position

    ComposeNode<MarkerNode, MapApplier>(
        factory = {
            MarkerNode(
                state = state,
                snippet = snippet,
                subSnippet = subSnippet,
                iconResId = iconResId,
                iconBitmap = iconBitmap,
                isIconClickable = isIconClickable,
                isAnimationEnabled = isAnimationEnabled,
                isInfoWindowDismissOnClick = isInfoWindowDismissOnClick,
                iconRotation = iconRotation,
                iconSize = iconSize,
                iconAnchor = iconAnchor,
                iconOffset = iconOffset,
                onClick = onClick,
            )
        },
        update = {
            update(state) { this.state = it }
            set(position) { this.position = it }
            set(snippet) { this.snippet = it }
            set(subSnippet) { this.subSnippet = it }
            set(iconResId) { this.iconResId = it }
            set(iconBitmap) { this.iconBitmap = it }
            set(isIconClickable) { this.isIconClickable = it }
            set(isAnimationEnabled) { this.isAnimationEnabled = it }
            set(isInfoWindowDismissOnClick) { this.isInfoWindowDismissOnClick = it }
            set(iconRotation) { this.iconRotation = it }
            set(iconSize) { this.iconSize = it }
            set(iconAnchor) { this.iconAnchor = it }
            set(iconOffset) { this.iconOffset = it }
            update(onClick) { this.onClick = it }
        },
    )
}

internal class MarkerNode(
    state: MarkerState,
    snippet: String?,
    subSnippet: String?,
    @DrawableRes iconResId: Int?,
    iconBitmap: Bitmap?,
    isIconClickable: Boolean,
    isAnimationEnabled: Boolean,
    isInfoWindowDismissOnClick: Boolean,
    iconRotation: Float,
    iconSize: Float,
    iconAnchor: String?,
    iconOffset: FloatArray?,
    onClick: (() -> Unit)?,
) : MapNode() {
    var state: MarkerState = state
        set(value) {
            field = value
            position = value.position
        }

    var position: OlaLatLng = state.position
        set(value) {
            field = value
            state.position = value
            marker?.setPosition(value)
        }

    var snippet: String? = snippet
        set(value) {
            field = value
            marker?.updateSnippet(value.orEmpty())
        }

    var subSnippet: String? = subSnippet
        set(value) {
            field = value
            marker?.updateSubSnippet(value.orEmpty())
        }

    var iconResId: Int? = iconResId
        set(value) {
            field = value
            if (value != null) {
                marker?.updateIconIntRes(value)
            }
        }

    var iconBitmap: Bitmap? = iconBitmap
        set(value) {
            field = value
            if (value != null) {
                marker?.updateIconBitmap(value)
            }
        }

    var isIconClickable: Boolean = isIconClickable
        set(value) {
            field = value
            refreshMarker()
        }

    var isAnimationEnabled: Boolean = isAnimationEnabled
        set(value) {
            field = value
            refreshMarker()
        }

    var isInfoWindowDismissOnClick: Boolean = isInfoWindowDismissOnClick
        set(value) {
            field = value
            refreshMarker()
        }

    var iconRotation: Float = iconRotation
        set(value) {
            field = value
            marker?.updateIconRotation(value)
        }

    var iconSize: Float = iconSize
        set(value) {
            field = value
            marker?.updateIconSize(value)
        }

    var iconAnchor: String? = iconAnchor
        set(value) {
            field = value
            if (value != null) {
                marker?.updateIconAnchor(value)
            }
        }

    var iconOffset: FloatArray? = iconOffset
        set(value) {
            field = value
            if (value != null) {
                marker?.updateIconOffset(value.toTypedArray())
            }
        }

    var onClick: (() -> Unit)? = onClick
        set(value) {
            field = value
            mapContext?.registerMarkerClickListener(markerId, value)
        }

    private var map: SdkOlaMap? = null
    private var mapContext: MapNodeContext? = null
    private var marker: SdkMarker? = null
    private val markerId = "compose-marker-${nextMarkerId()}"

    override fun onAttached(context: MapNodeContext) {
        mapContext = context
        map = context.map
        context.registerMarkerClickListener(markerId, onClick)
        marker = context.map.addMarker(buildOptions())
    }

    override fun onRemoved() {
        mapContext?.unregisterMarkerClickListener(markerId)
        marker?.removeMarker()
        marker = null
        map = null
        mapContext = null
    }

    private fun refreshMarker() {
        val sdkMarker = marker ?: return
        sdkMarker.updateMarker(buildOptions())
    }

    private fun buildOptions(): OlaMarkerOptions =
        OlaMarkerOptions.Builder()
            .setMarkerId(markerId)
            .setPosition(position)
            .setSnippet(snippet.orEmpty())
            .setSubSnippet(subSnippet.orEmpty())
            .setIsIconClickable(isIconClickable)
            .setIsAnimationEnable(isAnimationEnabled)
            .setIsInfoWindowDismissOnClick(isInfoWindowDismissOnClick)
            .setIconRotation(iconRotation)
            .setIconSize(iconSize)
            .apply {
                if (this@MarkerNode.iconResId != null) {
                    setIconIntRes(this@MarkerNode.iconResId)
                }
                if (this@MarkerNode.iconBitmap != null) {
                    setIconBitmap(this@MarkerNode.iconBitmap)
                }
                if (this@MarkerNode.iconAnchor != null) {
                    setIconAnchor(this@MarkerNode.iconAnchor)
                }
                if (this@MarkerNode.iconOffset != null) {
                    setIconOffset(this@MarkerNode.iconOffset!!.toTypedArray())
                }
            }
            .build()
}

private var markerIdCounter = 0

private fun nextMarkerId(): Int {
    markerIdCounter += 1
    return markerIdCounter
}

internal fun saveMarkerPosition(position: OlaLatLng): List<Double> =
    listOf(
        position.latitude,
        position.longitude,
        position.altitude,
    )

internal fun restoreMarkerPosition(values: List<*>): OlaLatLng? {
    if (values.size < 3) {
        return null
    }

    val latitude = values[0] as? Double ?: return null
    val longitude = values[1] as? Double ?: return null
    val altitude = values[2] as? Double ?: return null

    return OlaLatLng(
        latitude,
        longitude,
        altitude,
    )
}

internal val MarkerStateSaver: Saver<MarkerState, Any> = listSaver(
    save = { state ->
        saveMarkerPosition(state.position)
    },
    restore = { values ->
        restoreMarkerPosition(values)?.let { position ->
            MarkerState(position = position)
        }
    },
)
