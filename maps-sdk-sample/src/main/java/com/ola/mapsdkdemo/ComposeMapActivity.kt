package com.ola.mapsdkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ola.maps.compose.BezierCurve
import com.ola.maps.compose.Circle
import com.ola.maps.compose.ClusterItems
import com.ola.maps.compose.ClusterOptions
import com.ola.maps.compose.ClusteredMarkers
import com.ola.maps.compose.Marker
import com.ola.maps.compose.OlaMap
import com.ola.maps.compose.Polygon
import com.ola.maps.compose.Polyline
import com.ola.maps.compose.StrokePattern
import com.ola.maps.compose.rememberCameraPositionState
import com.ola.maps.compose.rememberMarkerState
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdkdemo.ui.theme.OlaMapSdkDemoTheme

class ComposeMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OlaMapSdkDemoTheme {
                ComposeMapScreen()
            }
        }
    }
}

@Composable
private fun ComposeMapScreen() {
    val olaCampus = OlaLatLng(12.931423492103944, 77.61648476788898)
    val routePoint = OlaLatLng(12.931758797710456, 77.61436504365439)
    val polygonPoint = OlaLatLng(12.9342, 77.6128)
    val polygonPoint2 = OlaLatLng(12.9294, 77.6117)
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState(position = olaCampus)
    val route = listOf(olaCampus, routePoint, polygonPoint)
    val polygon = listOf(olaCampus, polygonPoint, polygonPoint2)
    val clusterGeoJson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "properties": { "id": "cluster-1" },
              "geometry": { "type": "Point", "coordinates": [77.6164, 12.9314, 0] }
            },
            {
              "type": "Feature",
              "properties": { "id": "cluster-2" },
              "geometry": { "type": "Point", "coordinates": [77.6168, 12.9316, 0] }
            },
            {
              "type": "Feature",
              "properties": { "id": "cluster-3" },
              "geometry": { "type": "Point", "coordinates": [77.6171, 12.9319, 0] }
            }
          ]
        }
    """.trimIndent()

    LaunchedEffect(cameraPositionState) {
        cameraPositionState.easeTo(route + polygonPoint2, durationMs = 1200)
    }

    OlaMap(
        apiKey = BuildConfig.OLA_MAPS_API_KEY,
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        Marker(
            state = markerState,
            snippet = "Ola Campus",
            subSnippet = "Compose marker sample",
        )
        Polyline(
            points = route,
            color = Color(0xFFD32F2F),
            width = 6f,
            pattern = StrokePattern.Solid,
        )
        Polygon(
            points = polygon,
            fillColor = Color(0x332196F3),
        )
        Circle(
            center = routePoint,
            radius = 120f,
            fillColor = Color(0x334CAF50),
        )
        BezierCurve(
            start = polygonPoint2,
            end = routePoint,
            color = Color(0xFFFF9800),
            width = 5f,
            pattern = StrokePattern.Dotted,
        )
        ClusteredMarkers(
            items = ClusterItems.GeoJson(clusterGeoJson),
            options = ClusterOptions(
                clusterRadius = 40,
                defaultMarkerColor = Color(0xFF6A1B9A),
                defaultClusterColor = Color(0xFF00897B),
            ),
        )
    }
}
