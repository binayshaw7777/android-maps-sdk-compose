package com.ola.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.ola.mapsdk.camera.OlaCameraPosition
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.view.OlaMap

/**
 * State holder for reading and controlling the current camera position of [OlaMap].
 *
 * Operations called before the map becomes ready are queued and applied after binding.
 *
 * Example:
 * ```kotlin
 * val cameraPositionState = rememberCameraPositionState()
 *
 * Button(
 *     onClick = {
 *         cameraPositionState.move(
 *             target = OlaLatLng(12.931423492103944, 77.61648476788898),
 *             zoomLevel = 15.0,
 *             durationMs = 1000,
 *         )
 *     },
 * ) {
 *     Text("Move Camera")
 * }
 * ```
 */
@Stable
class CameraPositionState internal constructor(
    initialPosition: OlaCameraPosition?,
) {
    private var map: OlaMap? = null
    private val pendingOperations = mutableStateListOf<(OlaMap) -> Unit>()

    var position: OlaCameraPosition? = initialPosition
        internal set

    /**
     * Moves the camera to [target] at [zoomLevel].
     */
    fun move(
        target: OlaLatLng,
        zoomLevel: Double,
        durationMs: Int = 0,
    ) {
        dispatch { sdkMap ->
            sdkMap.moveCameraToLatLong(target, zoomLevel, durationMs)
            position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

    /**
     * Zooms to [target] using the SDK zoom helper.
     */
    fun zoomToLocation(
        target: OlaLatLng,
        zoomLevel: Double,
    ) {
        dispatch { sdkMap ->
            sdkMap.zoomToLocation(target, zoomLevel)
            position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

    /**
     * Fits the camera to a list of points using the Ola SDK ease-camera behavior.
     */
    fun easeTo(
        points: List<OlaLatLng>,
        durationMs: Int = 1000,
    ) {
        dispatch { sdkMap ->
            sdkMap.easeCamera(points, durationMs)
            position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

    /**
     * Applies a fully constructed [OlaCameraPosition].
     */
    fun update(position: OlaCameraPosition) {
        dispatch { sdkMap ->
            sdkMap.updateCameraPosition(position)
            this.position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

    internal fun bind(map: OlaMap) {
        this.map = map
        position = map.getCurrentOlaCameraPosition()
        if (pendingOperations.isNotEmpty()) {
            pendingOperations.toList().forEach { it(map) }
            pendingOperations.clear()
        }
    }

    internal fun unbind(map: OlaMap) {
        if (this.map === map) {
            position = map.getCurrentOlaCameraPosition()
            this.map = null
        }
    }

    private fun dispatch(operation: (OlaMap) -> Unit) {
        val currentMap = map
        if (currentMap != null) {
            operation(currentMap)
        } else {
            pendingOperations += operation
        }
    }
}

/**
 * Creates and remembers a [CameraPositionState].
 *
 * Example:
 * ```kotlin
 * val cameraPositionState = rememberCameraPositionState(
 *     initialPosition = OlaCameraPosition.Builder()
 *         .setTarget(OlaLatLng(12.931423492103944, 77.61648476788898))
 *         .setZoomLevel(14.0)
 *         .build(),
 * )
 * ```
 */
@Composable
fun rememberCameraPositionState(
    initialPosition: OlaCameraPosition? = null,
): CameraPositionState = rememberSaveable(
    saver = CameraPositionStateSaver,
) {
    CameraPositionState(initialPosition = initialPosition)
}

/**
 * Creates and remembers a [CameraPositionState] using an initialization block.
 *
 * Example:
 * ```kotlin
 * val cameraPositionState = rememberCameraPositionState {
 *     update(
 *         OlaCameraPosition.Builder()
 *             .setTarget(OlaLatLng(12.931423492103944, 77.61648476788898))
 *             .setZoomLevel(14.0)
 *             .build(),
 *     )
 * }
 * ```
 */
@Composable
fun rememberCameraPositionState(
    init: CameraPositionState.() -> Unit,
): CameraPositionState = rememberSaveable(
    saver = CameraPositionStateSaver,
) {
    CameraPositionState(initialPosition = null).apply(init)
}

internal fun saveOlaCameraPosition(position: OlaCameraPosition): List<Any> =
    listOfNotNull(
        position.target?.latitude,
        position.target?.longitude,
        position.target?.altitude,
        position.bearing,
        position.tilt,
        position.zoomLevel,
        position.duration,
        position.paddingStart,
        position.paddingTop,
        position.paddingEnd,
        position.paddingBottom,
    )

internal fun restoreOlaCameraPosition(values: List<Any>): OlaCameraPosition? {
    if (values.size < 11) {
        return null
    }

    return OlaCameraPosition.Builder()
        .setTarget(
            OlaLatLng(
                values[0] as Double,
                values[1] as Double,
                values[2] as Double,
            ),
        )
        .setBearing(values[3] as Double)
        .setTilt(values[4] as Double)
        .setZoomLevel(values[5] as Double)
        .setDuration(values[6] as Int)
        .setPaddingStart(values[7] as Int)
        .setPaddingTop(values[8] as Int)
        .setPaddingEnd(values[9] as Int)
        .setPaddingBottom(values[10] as Int)
        .build()
}

internal val OlaCameraPositionSaver: Saver<OlaCameraPosition, Any> = listSaver(
    save = { position ->
        saveOlaCameraPosition(position)
    },
    restore = ::restoreOlaCameraPosition,
)

internal val CameraPositionStateSaver: Saver<CameraPositionState, Any> = Saver(
    save = { state ->
        state.position?.let(::saveOlaCameraPosition)
    },
    restore = { restored ->
        CameraPositionState(
            initialPosition = (restored as? List<Any>)?.let(::restoreOlaCameraPosition),
        )
    },
)
