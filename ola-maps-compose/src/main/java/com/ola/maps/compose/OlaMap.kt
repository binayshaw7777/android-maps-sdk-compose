package com.ola.maps.compose

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ola.mapsdk.interfaces.OlaMapCallback
import com.ola.mapsdk.listeners.OlaMapsCameraListenerManager
import com.ola.mapsdk.listeners.OlaMapsListenerManager
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.view.OlaMapView
import com.ola.mapsdk.view.OlaMap as SdkOlaMap

@Composable
fun OlaMap(
    apiKey: String,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    properties: MapProperties = MapProperties(),
    uiSettings: MapUiSettings = MapUiSettings(),
    onMapReady: ((SdkOlaMap) -> Unit)? = null,
    onMapError: ((String) -> Unit)? = null,
    onMapClick: ((OlaLatLng) -> Unit)? = null,
    onMapLongClick: ((OlaLatLng) -> Unit)? = null,
    onCameraMove: (() -> Unit)? = null,
    onCameraIdle: (() -> Unit)? = null,
    content: @Composable @OlaMapComposable () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val currentOnMapReady by rememberUpdatedState(onMapReady)
    val currentOnMapError by rememberUpdatedState(onMapError)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMapLongClick by rememberUpdatedState(onMapLongClick)
    val currentOnCameraMove by rememberUpdatedState(onCameraMove)
    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    var mapView by remember { mutableStateOf<OlaMapView?>(null) }
    var map by remember { mutableStateOf<SdkOlaMap?>(null) }

    AndroidView(
        modifier = modifier,
        factory = {
            OlaMapView(it).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
                getMap(
                    apiKey = apiKey,
                    olaMapCallback = object : OlaMapCallback {
                        override fun onMapReady(olaMap: SdkOlaMap) {
                            map = olaMap
                            cameraPositionState.bind(olaMap)
                            currentOnMapReady?.invoke(olaMap)
                        }

                        override fun onMapError(error: String) {
                            currentOnMapError?.invoke(error)
                        }
                    },
                    mapControlSettings = uiSettings.toSdkSettings(),
                )
                mapView = this
            }
        },
        update = { view ->
            mapView = view
        },
    )

    DisposableEffect(lifecycleOwner, mapView) {
        val view = mapView
        if (view == null) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> view.onStart()
                    Lifecycle.Event.ON_RESUME -> view.onResume()
                    Lifecycle.Event.ON_PAUSE -> view.onPause()
                    Lifecycle.Event.ON_STOP -> view.onStop()
                    Lifecycle.Event.ON_DESTROY -> view.onDestroy()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    DisposableEffect(context, mapView) {
        val view = mapView
        if (view == null) {
            onDispose { }
        } else {
            val callbacks = mapViewLowMemoryCallbacks(view)
            context.registerComponentCallbacks(callbacks)
            onDispose {
                context.unregisterComponentCallbacks(callbacks)
            }
        }
    }

    LaunchedEffect(map, cameraPositionState) {
        map?.let(cameraPositionState::bind)
    }

    LaunchedEffect(map, uiSettings) {
        map?.updateMapUiSettings(uiSettings.toSdkSettings())
    }

    LaunchedEffect(map, properties.isMyLocationEnabled) {
        val sdkMap = map ?: return@LaunchedEffect
        if (properties.isMyLocationEnabled) {
            sdkMap.showCurrentLocation()
        } else {
            sdkMap.hideCurrentLocation()
        }
    }

    LaunchedEffect(map, currentOnMapClick, currentOnMapLongClick, currentOnCameraMove, currentOnCameraIdle) {
        val sdkMap = map ?: return@LaunchedEffect
        sdkMap.setOnMapClickedListener(object : OlaMapsListenerManager.OnOlaMapClickedListener {
            override fun onOlaMapClicked(olaLatLng: OlaLatLng) {
                cameraPositionState.position = sdkMap.getCurrentOlaCameraPosition()
                currentOnMapClick?.invoke(olaLatLng)
            }
        })
        sdkMap.setOnMapLongClickedListener(object : OlaMapsListenerManager.OnOlaMapsLongClickedListener {
            override fun onOlaMapsLongClicked(olaLatLng: OlaLatLng) {
                cameraPositionState.position = sdkMap.getCurrentOlaCameraPosition()
                currentOnMapLongClick?.invoke(olaLatLng)
            }
        })
        sdkMap.setOnOlaMapsCameraMoveListener(object : OlaMapsCameraListenerManager.OnOlaMapsCameraMoveListener {
            override fun onOlaMapsCameraMove() {
                cameraPositionState.position = sdkMap.getCurrentOlaCameraPosition()
                currentOnCameraMove?.invoke()
            }
        })
        sdkMap.setOnOlaMapsCameraIdleListener(object : OlaMapsCameraListenerManager.OnOlaMapsCameraIdleListener {
            override fun onOlaMapsCameraIdle() {
                cameraPositionState.position = sdkMap.getCurrentOlaCameraPosition()
                currentOnCameraIdle?.invoke()
            }
        })
    }

    DisposableEffect(map, cameraPositionState) {
        val sdkMap = map
        onDispose {
            if (sdkMap != null) {
                cameraPositionState.unbind(sdkMap)
            }
        }
    }

    DisposableEffect(map, parentComposition) {
        val sdkMap = map
        if (sdkMap == null) {
            onDispose { }
        } else {
            val composition = Composition(MapApplier(sdkMap), parentComposition)
            composition.setContent {
                currentContent()
            }
            onDispose {
                composition.dispose()
            }
        }
    }
}

private fun mapViewLowMemoryCallbacks(view: OlaMapView): ComponentCallbacks2 =
    object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit

        override fun onLowMemory() {
            view.onLowMemory()
        }

        override fun onTrimMemory(level: Int) {
            if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
                view.onLowMemory()
            }
        }
    }
