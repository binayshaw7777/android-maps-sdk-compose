package com.ola.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
): CameraPositionState = remember {
    CameraPositionState(initialPosition = initialPosition)
}

@Composable
fun rememberCameraPositionState(
    init: CameraPositionState.() -> Unit,
): CameraPositionState = remember {
    CameraPositionState(initialPosition = null).apply(init)
}
