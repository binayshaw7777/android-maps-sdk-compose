package com.ola.mapsdkdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ola.maps.compose.BezierCurve
import com.ola.maps.compose.Border
import com.ola.maps.compose.Circle
import com.ola.maps.compose.ClusterItems
import com.ola.maps.compose.ClusterOptions
import com.ola.maps.compose.ClusteredMarkers
import com.ola.maps.compose.MapProperties
import com.ola.maps.compose.MapUiSettings
import com.ola.maps.compose.Marker
import com.ola.maps.compose.OlaMap
import com.ola.maps.compose.Polygon
import com.ola.maps.compose.Polyline
import com.ola.maps.compose.StrokePattern
import com.ola.maps.compose.rememberCameraPositionState
import com.ola.maps.compose.rememberMarkerState
import com.ola.mapsdk.camera.OlaCameraPosition
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaMarkerOptions
import com.ola.mapsdk.view.Marker as SdkMarker
import com.ola.mapsdk.view.OlaMap as SdkOlaMap
import org.maplibre.android.style.layers.Property

class ComposeMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                ComposeMapPlayground()
            }
        }
    }
}

@Composable
private fun ComposeMapPlayground() {
    val context = LocalContext.current
    val initialCameraPosition = remember {
        OlaCameraPosition.Builder()
            .setTarget(location1)
            .setZoomLevel(14.0)
            .build()
    }
    val cameraPositionState = rememberCameraPositionState(initialPosition = initialCameraPosition)
    val markerState = rememberMarkerState(position = location1)

    var sdkMap by remember { mutableStateOf<SdkOlaMap?>(null) }
    var infoWindowMarker by remember { mutableStateOf<SdkMarker?>(null) }
    var selectedSection by rememberSaveable { mutableStateOf(DemoSection.Marker) }
    var eventMessage by rememberSaveable { mutableStateOf("Map ready for Compose playground actions.") }

    var showUiSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var myLocationEnabled by rememberSaveable { mutableStateOf(false) }

    var markerVisible by rememberSaveable { mutableStateOf(false) }
    var markerSnippet by rememberSaveable { mutableStateOf("Compose marker playground") }
    var markerSubSnippet by rememberSaveable { mutableStateOf("Tap actions below to mutate the marker.") }
    var markerSize by rememberSaveable { mutableStateOf(1f) }
    var markerRotation by rememberSaveable { mutableStateOf(0f) }
    var markerAnchor by rememberSaveable { mutableStateOf<String?>(null) }
    var markerOffsetX by rememberSaveable { mutableStateOf(0f) }
    var markerOffsetY by rememberSaveable { mutableStateOf(0f) }
    var markerIconRes by rememberSaveable { mutableStateOf(R.drawable.ic_top_truck) }
    var fitViewMarkersVisible by rememberSaveable { mutableStateOf(false) }

    var polylineVisible by rememberSaveable { mutableStateOf(false) }
    var polylineColor by remember { mutableStateOf(Color(0xFF0F766E)) }

    var polygonVisible by rememberSaveable { mutableStateOf(false) }
    var polygonColor by remember { mutableStateOf(Color(0xFF2563EB).copy(alpha = 0.26f)) }

    var circleVisible by rememberSaveable { mutableStateOf(false) }
    var circleColor by remember { mutableStateOf(Color(0xFFDC2626).copy(alpha = 0.25f)) }

    var bezierVisible by rememberSaveable { mutableStateOf(false) }
    var bezierColor by remember { mutableStateOf(Color(0xFFF97316)) }

    var clustersVisible by rememberSaveable { mutableStateOf(false) }
    var clusterVariant by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        myLocationEnabled = granted
        eventMessage = if (granted) {
            "Current location enabled."
        } else {
            "Location permission denied."
        }
        if (granted) {
            sdkMap?.showCurrentLocation()
            sdkMap?.getCurrentLocation()?.let {
                cameraPositionState.move(it, 15.0, durationMs = 800)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OlaMap(
            apiKey = BuildConfig.OLA_MAPS_API_KEY,
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = myLocationEnabled),
            uiSettings = uiSettings,
            onMapReady = {
                sdkMap = it
                eventMessage = "Compose sample loaded. Pick a section and start mutating the map."
            },
            onMapError = {
                eventMessage = "Map error: $it"
            },
            onMapClick = {
                eventMessage = "Map click at ${it.latitude.formatCoordinate()}, ${it.longitude.formatCoordinate()}"
            },
            onMapLongClick = {
                eventMessage = "Map long click at ${it.latitude.formatCoordinate()}, ${it.longitude.formatCoordinate()}"
            },
            onCameraMove = {
                eventMessage = "Camera moving"
            },
            onCameraIdle = {
                eventMessage = "Camera idle"
            },
        ) {
            if (markerVisible) {
                Marker(
                    state = markerState,
                    snippet = markerSnippet,
                    subSnippet = markerSubSnippet,
                    iconResId = markerIconRes,
                    iconSize = markerSize,
                    iconRotation = markerRotation,
                    iconAnchor = markerAnchor,
                    iconOffset = floatArrayOf(markerOffsetX, markerOffsetY),
                    isAnimationEnabled = true,
                    onClick = {
                        eventMessage = "Compose marker clicked"
                    },
                )
            }

            if (fitViewMarkersVisible) {
                Marker(
                    state = rememberMarkerState(position = location2),
                    iconResId = R.drawable.ic_dot,
                    iconSize = 0.8f,
                    snippet = "Location 2",
                )
                Marker(
                    state = rememberMarkerState(position = location3),
                    iconResId = R.drawable.ic_dot,
                    iconSize = 0.8f,
                    snippet = "Location 3",
                )
                Marker(
                    state = rememberMarkerState(position = location4),
                    iconResId = R.drawable.ic_dot,
                    iconSize = 0.8f,
                    snippet = "Location 4",
                )
            }

            if (polylineVisible) {
                Polyline(
                    points = listOf(location1, location2),
                    color = polylineColor,
                    width = 6f,
                    pattern = StrokePattern.Solid,
                )
            }

            if (polygonVisible) {
                Polygon(
                    points = polygonPoints,
                    fillColor = polygonColor,
                    opacity = polygonColor.alpha,
                    border = Border(
                        color = Color(0xFF1D4ED8),
                        width = 2f,
                        pattern = StrokePattern.Dotted,
                        dashArray = floatArrayOf(2f, 2f),
                    ),
                )
            }

            if (circleVisible) {
                Circle(
                    center = location1,
                    radius = 100f,
                    fillColor = circleColor,
                    opacity = circleColor.alpha,
                    border = Border(
                        color = Color(0xFF991B1B),
                        width = 2f,
                    ),
                )
            }

            if (bezierVisible) {
                BezierCurve(
                    start = location1,
                    end = location2,
                    color = bezierColor,
                    width = 5f,
                    curveFactor = 0.45f,
                    etaMessage = "12 min",
                    etaBackgroundColor = Color(0xFF111827),
                    etaTextColor = Color.White,
                )
            }

            if (clustersVisible) {
                ClusteredMarkers(
                    items = ClusterItems.GeoJson(
                        if (clusterVariant) alternateClusterGeoJson else defaultClusterGeoJson,
                    ),
                    options = if (clusterVariant) {
                        ClusterOptions(
                            clusterRadius = 45,
                            defaultMarkerColor = Color(0xFFF97316),
                            defaultClusterColor = Color(0xFF7C2D12),
                            stop1Color = Color(0xFFF59E0B),
                            stop2Color = Color(0xFFEA580C),
                            textColor = Color.White,
                        )
                    } else {
                        ClusterOptions(
                            clusterRadius = 35,
                            defaultMarkerColor = Color(0xFF06B6D4),
                            defaultClusterColor = Color(0xFFDC2626),
                            stop1Color = Color(0xFF16A34A),
                            stop2Color = Color(0xFF2563EB),
                            textColor = Color.White,
                        )
                    },
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
        ) {
            SectionChips(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it },
            )
            Spacer(modifier = Modifier.height(12.dp))
            EventBanner(
                title = selectedSection.title,
                message = if (selectedSection == DemoSection.MapEvents) {
                    "$eventMessage. Interact directly with the map to inspect callbacks."
                } else {
                    eventMessage
                },
            )
        }

        ActionsPanel(
            section = selectedSection,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            onOpenUiSettings = {
                showUiSettingsDialog = true
            },
            onZoomIn = {
                val target = cameraPositionState.position?.target ?: markerState.position
                cameraPositionState.zoomToLocation(target, (cameraPositionState.position?.zoomLevel ?: 14.0) + 1.0)
                eventMessage = "Zoomed in"
            },
            onZoomOut = {
                val target = cameraPositionState.position?.target ?: markerState.position
                cameraPositionState.zoomToLocation(target, (cameraPositionState.position?.zoomLevel ?: 14.0) - 1.0)
                eventMessage = "Zoomed out"
            },
            onShowCurrentLocation = {
                val fineGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
                if (fineGranted || coarseGranted) {
                    myLocationEnabled = true
                    sdkMap?.showCurrentLocation()
                    sdkMap?.getCurrentLocation()?.let {
                        cameraPositionState.move(it, 15.0, durationMs = 800)
                    }
                    eventMessage = "Current location enabled."
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                }
            },
            onHideCurrentLocation = {
                myLocationEnabled = false
                sdkMap?.hideCurrentLocation()
                eventMessage = "Current location hidden."
            },
            onAddMarker = {
                markerVisible = true
                fitViewMarkersVisible = false
                markerState.position = location1
                markerSnippet = "Compose marker playground"
                markerSubSnippet = "Tap actions below to mutate the marker."
                markerSize = 1f
                markerRotation = 0f
                markerAnchor = null
                markerOffsetX = 0f
                markerOffsetY = 0f
                markerIconRes = R.drawable.ic_top_truck
                cameraPositionState.move(location1, 15.0, durationMs = 1000)
                eventMessage = "Marker added."
            },
            onRemoveMarker = {
                markerVisible = false
                fitViewMarkersVisible = false
                eventMessage = "Marker removed."
            },
            onSetMarkerPosition = {
                markerVisible = true
                markerState.position = location2
                eventMessage = "Marker moved to second location."
            },
            onUpdateMarkerSize = {
                markerVisible = true
                markerSize = if (markerSize < 1.5f) 1.5f else 1f
                eventMessage = "Marker size updated to ${markerSize}x."
            },
            onUpdateMarkerRotation = {
                markerVisible = true
                markerRotation = if (markerRotation < 45f) 45f else 0f
                eventMessage = "Marker rotation updated to ${markerRotation.toInt()} degrees."
            },
            onUpdateMarkerInfo = {
                markerVisible = true
                markerSnippet = "Updated marker info - size ${markerSize}x"
                markerSubSnippet = "Rotation ${markerRotation.toInt()} degrees"
                eventMessage = "Marker snippet updated."
            },
            onUpdateMarkerOffset = {
                markerVisible = true
                val applyOffset = markerOffsetX == 0f && markerOffsetY == 0f
                markerOffsetX = if (applyOffset) 10f else 0f
                markerOffsetY = if (applyOffset) -5f else 0f
                eventMessage = "Marker offset ${if (applyOffset) "applied" else "reset"}."
            },
            onUpdateMarkerAnchor = {
                markerVisible = true
                markerAnchor = if (markerAnchor == Property.ICON_ANCHOR_BOTTOM) null else Property.ICON_ANCHOR_BOTTOM
                eventMessage = "Marker anchor ${if (markerAnchor == null) "reset" else "set to bottom"}."
            },
            onChangeMarkerIcon = {
                markerVisible = true
                markerIconRes = if (markerIconRes == R.drawable.ic_top_truck) {
                    R.drawable.ic_car_top
                } else {
                    R.drawable.ic_top_truck
                }
                eventMessage = "Marker icon switched."
            },
            onFitMarkerView = {
                markerVisible = true
                fitViewMarkersVisible = true
                cameraPositionState.easeTo(listOf(location1, location2, location3, location4), durationMs = 2000)
                eventMessage = "Camera fitted to all marker points."
            },
            onAddInfoWindow = {
                val map = sdkMap ?: return@ActionsPanel
                map.moveCameraToLatLong(location1, 15.0, durationMs = 1000)
                infoWindowMarker?.removeMarker()
                infoWindowMarker = map.addMarker(
                    OlaMarkerOptions.Builder()
                        .setMarkerId("compose-info-window")
                        .setPosition(location1)
                        .setSnippet("This is the info window")
                        .setIsIconClickable(true)
                        .setIsInfoWindowDismissOnClick(true)
                        .build(),
                )
                eventMessage = "Info window marker added."
            },
            onHideInfoWindow = {
                infoWindowMarker?.hideInfoWindow()
                eventMessage = "Info window hidden."
            },
            onUpdateInfoWindow = {
                infoWindowMarker?.updateInfoWindow("This is updated info window")
                eventMessage = "Info window updated."
            },
            onAddPolyline = {
                polylineVisible = true
                cameraPositionState.move(location1, 15.0, durationMs = 1000)
                eventMessage = "Polyline added."
            },
            onRemovePolyline = {
                polylineVisible = false
                eventMessage = "Polyline removed."
            },
            onUpdatePolyline = {
                polylineVisible = true
                polylineColor = if (polylineColor == Color(0xFFDC2626)) Color(0xFF0F766E) else Color(0xFFDC2626)
                eventMessage = "Polyline color updated."
            },
            onAddCircle = {
                circleVisible = true
                cameraPositionState.move(location1, 15.0, durationMs = 1000)
                eventMessage = "Circle added."
            },
            onRemoveCircle = {
                circleVisible = false
                eventMessage = "Circle removed."
            },
            onUpdateCircle = {
                circleVisible = true
                circleColor = if (circleColor.red > 0.7f) {
                    Color(0xFF2563EB).copy(alpha = 0.25f)
                } else {
                    Color(0xFFDC2626).copy(alpha = 0.25f)
                }
                eventMessage = "Circle color updated."
            },
            onAddPolygon = {
                polygonVisible = true
                cameraPositionState.move(polygonCameraTarget, 15.0, durationMs = 1000)
                eventMessage = "Polygon added."
            },
            onRemovePolygon = {
                polygonVisible = false
                eventMessage = "Polygon removed."
            },
            onUpdatePolygon = {
                polygonVisible = true
                polygonColor = if (polygonColor.blue > 0.6f) {
                    Color(0xFFDC2626).copy(alpha = 0.24f)
                } else {
                    Color(0xFF2563EB).copy(alpha = 0.26f)
                }
                eventMessage = "Polygon color updated."
            },
            onAddBezier = {
                bezierVisible = true
                cameraPositionState.move(location1, 15.0, durationMs = 1000)
                eventMessage = "Bezier curve added."
            },
            onRemoveBezier = {
                bezierVisible = false
                eventMessage = "Bezier curve removed."
            },
            onUpdateBezier = {
                bezierVisible = true
                bezierColor = if (bezierColor == Color(0xFFDC2626)) Color(0xFFF97316) else Color(0xFFDC2626)
                eventMessage = "Bezier curve color updated."
            },
            onAddClusters = {
                clustersVisible = true
                clusterVariant = false
                cameraPositionState.move(clusterCenter, 4.8, durationMs = 1000)
                eventMessage = "Clustered markers added."
            },
            onRemoveClusters = {
                clustersVisible = false
                eventMessage = "Clustered markers removed."
            },
            onUpdateClusters = {
                clustersVisible = true
                clusterVariant = !clusterVariant
                cameraPositionState.move(clusterCenter, 4.8, durationMs = 1000)
                eventMessage = "Clustered marker dataset updated."
            },
        )
    }

    if (showUiSettingsDialog) {
        UiSettingsDialog(
            value = uiSettings,
            onDismiss = { showUiSettingsDialog = false },
            onApply = {
                uiSettings = it
                showUiSettingsDialog = false
                eventMessage = "UI settings updated."
            },
        )
    }
}

@Composable
private fun SectionChips(
    selectedSection: DemoSection,
    onSectionSelected: (DemoSection) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DemoSection.entries.forEach { section ->
            OutlinedButton(
                onClick = { onSectionSelected(section) },
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = section.title,
                    color = if (section == selectedSection) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
private fun EventBanner(
    title: String,
    message: String,
) {
    Surface(
        color = Color(0xE6111827),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE5E7EB),
            )
        }
    }
}

@Composable
private fun ActionsPanel(
    section: DemoSection,
    modifier: Modifier = Modifier,
    onOpenUiSettings: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onShowCurrentLocation: () -> Unit,
    onHideCurrentLocation: () -> Unit,
    onAddMarker: () -> Unit,
    onRemoveMarker: () -> Unit,
    onSetMarkerPosition: () -> Unit,
    onUpdateMarkerSize: () -> Unit,
    onUpdateMarkerRotation: () -> Unit,
    onUpdateMarkerInfo: () -> Unit,
    onUpdateMarkerOffset: () -> Unit,
    onUpdateMarkerAnchor: () -> Unit,
    onChangeMarkerIcon: () -> Unit,
    onFitMarkerView: () -> Unit,
    onAddInfoWindow: () -> Unit,
    onHideInfoWindow: () -> Unit,
    onUpdateInfoWindow: () -> Unit,
    onAddPolyline: () -> Unit,
    onRemovePolyline: () -> Unit,
    onUpdatePolyline: () -> Unit,
    onAddCircle: () -> Unit,
    onRemoveCircle: () -> Unit,
    onUpdateCircle: () -> Unit,
    onAddPolygon: () -> Unit,
    onRemovePolygon: () -> Unit,
    onUpdatePolygon: () -> Unit,
    onAddBezier: () -> Unit,
    onRemoveBezier: () -> Unit,
    onUpdateBezier: () -> Unit,
    onAddClusters: () -> Unit,
    onRemoveClusters: () -> Unit,
    onUpdateClusters: () -> Unit,
) {
    val actions = remember(section) {
        when (section) {
            DemoSection.UiControls -> listOf(
                PlaygroundAction("Zoom In", onZoomIn),
                PlaygroundAction("Zoom Out", onZoomOut),
            )

            DemoSection.UiSettings -> listOf(
                PlaygroundAction("Open Settings", onOpenUiSettings),
            )

            DemoSection.CurrentLocation -> listOf(
                PlaygroundAction("Show", onShowCurrentLocation),
                PlaygroundAction("Hide", onHideCurrentLocation),
            )

            DemoSection.Marker -> listOf(
                PlaygroundAction("Add", onAddMarker),
                PlaygroundAction("Remove", onRemoveMarker),
                PlaygroundAction("Set Position", onSetMarkerPosition),
                PlaygroundAction("Update Size", onUpdateMarkerSize),
                PlaygroundAction("Update Rotation", onUpdateMarkerRotation),
                PlaygroundAction("Update Info", onUpdateMarkerInfo),
                PlaygroundAction("Update Offset", onUpdateMarkerOffset),
                PlaygroundAction("Update Anchor", onUpdateMarkerAnchor),
                PlaygroundAction("Change Icon", onChangeMarkerIcon),
                PlaygroundAction("Fit View", onFitMarkerView),
            )

            DemoSection.MarkerClustering -> listOf(
                PlaygroundAction("Add", onAddClusters),
                PlaygroundAction("Remove", onRemoveClusters),
                PlaygroundAction("Update", onUpdateClusters),
            )

            DemoSection.Infowindow -> listOf(
                PlaygroundAction("Add", onAddInfoWindow),
                PlaygroundAction("Hide", onHideInfoWindow),
                PlaygroundAction("Update", onUpdateInfoWindow),
            )

            DemoSection.Circle -> listOf(
                PlaygroundAction("Add", onAddCircle),
                PlaygroundAction("Remove", onRemoveCircle),
                PlaygroundAction("Update", onUpdateCircle),
            )

            DemoSection.Polygon -> listOf(
                PlaygroundAction("Add", onAddPolygon),
                PlaygroundAction("Remove", onRemovePolygon),
                PlaygroundAction("Update", onUpdatePolygon),
            )

            DemoSection.BezierCurve -> listOf(
                PlaygroundAction("Add", onAddBezier),
                PlaygroundAction("Remove", onRemoveBezier),
                PlaygroundAction("Update", onUpdateBezier),
            )

            DemoSection.Polyline -> listOf(
                PlaygroundAction("Add", onAddPolyline),
                PlaygroundAction("Remove", onRemovePolyline),
                PlaygroundAction("Update", onUpdatePolyline),
            )

            DemoSection.MapEvents -> emptyList()
        }
    }

    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xF7FFFBEB),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF374151),
            )
            if (actions.isEmpty()) {
                Text(
                    text = "Interact with the map directly to inspect click, long-click, move, and idle callbacks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                )
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    actions.forEach { action ->
                        OutlinedButton(
                            onClick = action.onClick,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            Text(action.label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UiSettingsDialog(
    value: MapUiSettings,
    onDismiss: () -> Unit,
    onApply: (MapUiSettings) -> Unit,
) {
    var rotate by remember(value) { mutableStateOf(value.isRotateGesturesEnabled) }
    var scroll by remember(value) { mutableStateOf(value.isScrollGesturesEnabled) }
    var zoom by remember(value) { mutableStateOf(value.isZoomGesturesEnabled) }
    var compass by remember(value) { mutableStateOf(value.isCompassEnabled) }
    var tilt by remember(value) { mutableStateOf(value.isTiltGesturesEnabled) }
    var doubleTap by remember(value) { mutableStateOf(value.isDoubleTapGesturesEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("UI Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingRow("Rotate gestures", rotate) { rotate = it }
                SettingRow("Scroll gestures", scroll) { scroll = it }
                SettingRow("Zoom gestures", zoom) { zoom = it }
                SettingRow("Compass", compass) { compass = it }
                SettingRow("Tilt gestures", tilt) { tilt = it }
                SettingRow("Double tap", doubleTap) { doubleTap = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(
                        MapUiSettings(
                            isRotateGesturesEnabled = rotate,
                            isScrollGesturesEnabled = scroll,
                            isZoomGesturesEnabled = zoom,
                            isCompassEnabled = compass,
                            isTiltGesturesEnabled = tilt,
                            isDoubleTapGesturesEnabled = doubleTap,
                        ),
                    )
                },
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

private data class PlaygroundAction(
    val label: String,
    val onClick: () -> Unit,
)

private enum class DemoSection(
    val title: String,
    val description: String,
) {
    UiControls("UI Controls", "Mirror the XML zoom controls and mutate camera scale directly."),
    UiSettings("UI Settings", "Open the Compose settings sheet and toggle the same gesture and compass flags."),
    CurrentLocation("Current Location", "Request location permission, show the user puck, and move the camera to it."),
    Marker("Marker", "Exercise the Compose marker API with the same add, mutate, and fit-view actions."),
    MarkerClustering("Marker Clustering", "Toggle clustered datasets and verify updates without leaving Compose."),
    Infowindow("Infowindow", "Drive the raw SDK info window APIs from the Compose sample while the wrapper catches up."),
    Circle("Circle", "Add, remove, and recolor circle overlays in the Compose runtime."),
    Polygon("Polygon", "Add, remove, and recolor polygon overlays with border styling."),
    BezierCurve("Bezier Curve", "Add, remove, and recolor bezier route overlays with ETA styling."),
    Polyline("Polyline", "Add, remove, and recolor a basic route line between sample coordinates."),
    MapEvents("Map Events", "Tap and move the map to inspect the callback stream inside the Compose sample."),
}

private fun Double.formatCoordinate(): String = String.format("%.5f", this)

private val location1 = OlaLatLng(12.931423492103944, 77.61648476788898)
private val location2 = OlaLatLng(12.931758797710456, 77.61436504365439)
private val location3 = OlaLatLng(19.305924882853862, 72.78733286147022)
private val location4 = OlaLatLng(12.925000, 77.610000)
private val polygonCameraTarget = OlaLatLng(12.964623365273798, 77.59560740718187)
private val clusterCenter = OlaLatLng(45.0, -120.0)

private val polygonPoints = listOf(
    OlaLatLng(12.970467915225672, 77.58815217917578),
    OlaLatLng(12.962675151452494, 77.57848953729078),
    OlaLatLng(12.964623365273798, 77.59560740718187),
)

private const val defaultClusterGeoJson = """
{
  "type": "FeatureCollection",
  "features": [
    { "type": "Feature", "properties": { "id": "p1" }, "geometry": { "type": "Point", "coordinates": [ -122.4194, 37.7749, 0 ] } },
    { "type": "Feature", "properties": { "id": "p2" }, "geometry": { "type": "Point", "coordinates": [ -122.4140, 37.7792, 0 ] } },
    { "type": "Feature", "properties": { "id": "p3" }, "geometry": { "type": "Point", "coordinates": [ -122.4090, 37.7815, 0 ] } },
    { "type": "Feature", "properties": { "id": "p4" }, "geometry": { "type": "Point", "coordinates": [ -118.2437, 34.0522, 0 ] } },
    { "type": "Feature", "properties": { "id": "p5" }, "geometry": { "type": "Point", "coordinates": [ -118.2480, 34.0490, 0 ] } },
    { "type": "Feature", "properties": { "id": "p6" }, "geometry": { "type": "Point", "coordinates": [ -118.2395, 34.0460, 0 ] } },
    { "type": "Feature", "properties": { "id": "p7" }, "geometry": { "type": "Point", "coordinates": [ -87.6298, 41.8781, 0 ] } },
    { "type": "Feature", "properties": { "id": "p8" }, "geometry": { "type": "Point", "coordinates": [ -87.6200, 41.8810, 0 ] } },
    { "type": "Feature", "properties": { "id": "p9" }, "geometry": { "type": "Point", "coordinates": [ -74.0060, 40.7128, 0 ] } },
    { "type": "Feature", "properties": { "id": "p10" }, "geometry": { "type": "Point", "coordinates": [ -73.9990, 40.7160, 0 ] } }
  ]
}
"""

private const val alternateClusterGeoJson = """
{
  "type": "FeatureCollection",
  "features": [
    { "type": "Feature", "properties": { "id": "u1" }, "geometry": { "type": "Point", "coordinates": [ 72.8777, 19.0760, 0 ] } },
    { "type": "Feature", "properties": { "id": "u2" }, "geometry": { "type": "Point", "coordinates": [ 72.8840, 19.0720, 0 ] } },
    { "type": "Feature", "properties": { "id": "u3" }, "geometry": { "type": "Point", "coordinates": [ 77.5946, 12.9716, 0 ] } },
    { "type": "Feature", "properties": { "id": "u4" }, "geometry": { "type": "Point", "coordinates": [ 77.6000, 12.9750, 0 ] } },
    { "type": "Feature", "properties": { "id": "u5" }, "geometry": { "type": "Point", "coordinates": [ 77.6100, 12.9650, 0 ] } },
    { "type": "Feature", "properties": { "id": "u6" }, "geometry": { "type": "Point", "coordinates": [ 88.3639, 22.5726, 0 ] } },
    { "type": "Feature", "properties": { "id": "u7" }, "geometry": { "type": "Point", "coordinates": [ 88.3690, 22.5750, 0 ] } },
    { "type": "Feature", "properties": { "id": "u8" }, "geometry": { "type": "Point", "coordinates": [ 77.2090, 28.6139, 0 ] } },
    { "type": "Feature", "properties": { "id": "u9" }, "geometry": { "type": "Point", "coordinates": [ 77.2140, 28.6170, 0 ] } },
    { "type": "Feature", "properties": { "id": "u10" }, "geometry": { "type": "Point", "coordinates": [ 77.2190, 28.6100, 0 ] } }
  ]
}
"""
