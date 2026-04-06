package com.ola.mapsdkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ola.maps.compose.Marker
import com.ola.maps.compose.OlaMap
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
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState(position = olaCampus)

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
    }
}
