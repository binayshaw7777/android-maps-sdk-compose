package com.ola.maps.compose

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ola.mapsdk.model.FeatureCollection
import com.ola.mapsdk.model.OlaMarkerClusterOptions
import com.ola.mapsdk.view.ClusteredMarkers as SdkClusteredMarkers
import com.ola.mapsdk.view.OlaMap as SdkOlaMap

/**
 * Supported inputs for [ClusteredMarkers].
 */
@Immutable
sealed interface ClusterItems {
    /**
     * Raw GeoJSON string input.
     */
    @Immutable
    data class GeoJson(
        val value: String,
    ) : ClusterItems

    /**
     * Parsed `FeatureCollection` input from the Ola SDK model.
     */
    @Immutable
    data class FeatureCollectionData(
        val value: FeatureCollection,
    ) : ClusterItems
}

/**
 * Styling and behavior options for [ClusteredMarkers].
 *
 * Example:
 * ```kotlin
 * val options = ClusterOptions(
 *     clusterRadius = 40,
 *     defaultMarkerColor = Color.Red,
 *     defaultClusterColor = Color(0xFF1D4ED8),
 *     textColor = Color.White,
 * )
 * ```
 */
@Immutable
data class ClusterOptions(
    val clusterRadius: Int = 50,
    val defaultMarkerColor: Color = Color.Red,
    val defaultClusterColor: Color = Color(0xFF2E7D32),
    val defaultMarkerIcon: Bitmap? = null,
    val stop1Color: Color? = null,
    val stop2Color: Color? = null,
    val textSize: Float = 12f,
    val textColor: Color = Color.White,
)

/**
 * Adds clustered markers to [OlaMap].
 *
 * Example:
 * ```kotlin
 * OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
 *     ClusteredMarkers(
 *         items = ClusterItems.GeoJson(geoJsonString),
 *         options = ClusterOptions(clusterRadius = 40),
 *     )
 * }
 * ```
 */
@Composable
@OlaMapComposable
fun ClusteredMarkers(
    items: ClusterItems,
    options: ClusterOptions = ClusterOptions(),
) {
    ComposeNode<ClusteredMarkersNode, MapApplier>(
        factory = {
            ClusteredMarkersNode(
                items = items,
                options = options,
            )
        },
        update = {
            set(items) { this.items = it }
            set(options) { this.options = it }
        },
    )
}

internal class ClusteredMarkersNode(
    items: ClusterItems,
    options: ClusterOptions,
) : MapNode() {
    var items: ClusterItems = items
        set(value) {
            field = value
            updateClusteredMarkers()
        }

    var options: ClusterOptions = options
        set(value) {
            field = value
            updateClusteredMarkers()
        }

    private var map: SdkOlaMap? = null
    private var clusteredMarkers: SdkClusteredMarkers? = null

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        clusteredMarkers = addClusteredMarkers(context.map)
    }

    override fun onRemoved() {
        clusteredMarkers?.removeClusteredMarkers()
        clusteredMarkers = null
        map = null
    }

    private fun updateClusteredMarkers() {
        val sdkClusteredMarkers = clusteredMarkers
        if (sdkClusteredMarkers != null) {
            when (val currentItems = items) {
                is ClusterItems.FeatureCollectionData -> {
                    sdkClusteredMarkers.updateClusteredMarkers(
                        currentItems.value,
                        options.toSdkOptions(),
                    )
                }

                is ClusterItems.GeoJson -> {
                    sdkClusteredMarkers.updateClusteredMarkers(
                        currentItems.value,
                        options.toSdkOptions(),
                    )
                }
            }
        } else {
            val sdkMap = map ?: return
            clusteredMarkers = addClusteredMarkers(sdkMap)
        }
    }

    private fun addClusteredMarkers(map: SdkOlaMap): SdkClusteredMarkers =
        when (val currentItems = items) {
            is ClusterItems.FeatureCollectionData -> {
                map.addClusteredMarkers(options.toSdkOptions(), currentItems.value)
            }

            is ClusterItems.GeoJson -> {
                map.addClusteredMarkers(options.toSdkOptions(), currentItems.value)
            }
        }
}

internal fun ClusterOptions.toSdkOptions(): OlaMarkerClusterOptions =
    run {
        val markerIcon = defaultMarkerIcon
        val firstStopColor = stop1Color
        val secondStopColor = stop2Color

        OlaMarkerClusterOptions.Builder()
            .setClusterRadius(clusterRadius)
            .setDefaultMarkerColor(defaultMarkerColor.toHexString())
            .setDefaultClusterColor(defaultClusterColor.toHexString())
            .setTextSize(textSize)
            .setTextColor(textColor.toHexString())
            .apply {
                if (markerIcon != null) {
                    setDefaultMarkerIcon(markerIcon)
                }
                if (firstStopColor != null) {
                    setStop1Color(firstStopColor.toHexString())
                }
                if (secondStopColor != null) {
                    setStop2Color(secondStopColor.toHexString())
                }
            }
            .build()
    }
