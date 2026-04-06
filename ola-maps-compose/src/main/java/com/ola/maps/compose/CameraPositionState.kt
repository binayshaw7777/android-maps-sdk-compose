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

@Stable
class CameraPositionState internal constructor(
    initialPosition: OlaCameraPosition?,
) {
    private var map: OlaMap? = null
    private val pendingOperations = mutableStateListOf<(OlaMap) -> Unit>()

    var position: OlaCameraPosition? = initialPosition
        internal set

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

    fun zoomToLocation(
        target: OlaLatLng,
        zoomLevel: Double,
    ) {
        dispatch { sdkMap ->
            sdkMap.zoomToLocation(target, zoomLevel)
            position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

    fun easeTo(
        points: List<OlaLatLng>,
        durationMs: Int = 1000,
    ) {
        dispatch { sdkMap ->
            sdkMap.easeCamera(points, durationMs)
            position = sdkMap.getCurrentOlaCameraPosition()
        }
    }

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

@Composable
fun rememberCameraPositionState(
    initialPosition: OlaCameraPosition? = null,
): CameraPositionState = rememberSaveable(
    saver = CameraPositionStateSaver,
) {
    CameraPositionState(initialPosition = initialPosition)
}

@Composable
fun rememberCameraPositionState(
    init: CameraPositionState.() -> Unit,
): CameraPositionState = rememberSaveable(
    saver = CameraPositionStateSaver,
) {
    CameraPositionState(initialPosition = null).apply(init)
}

private fun saveOlaCameraPosition(position: OlaCameraPosition): List<Any> =
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

private fun restoreOlaCameraPosition(values: List<Any>): OlaCameraPosition? {
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

private val OlaCameraPositionSaver: Saver<OlaCameraPosition, Any> = listSaver(
    save = { position ->
        saveOlaCameraPosition(position)
    },
    restore = ::restoreOlaCameraPosition,
)

private val CameraPositionStateSaver: Saver<CameraPositionState, Any> = Saver(
    save = { state ->
        state.position?.let(::saveOlaCameraPosition)
    },
    restore = { restored ->
        CameraPositionState(
            initialPosition = (restored as? List<Any>)?.let(::restoreOlaCameraPosition),
        )
    },
)
