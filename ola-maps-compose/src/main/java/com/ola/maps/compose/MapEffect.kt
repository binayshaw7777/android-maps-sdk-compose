package com.ola.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import com.ola.mapsdk.view.OlaMap as SdkOlaMap

/**
 * Runs imperative work against the raw Ola SDK map instance from within [OlaMap] content.
 *
 * Use this when a feature is not yet available as a dedicated declarative wrapper.
 * Re-executes when [keys] change.
 *
 * Example:
 * ```kotlin
 * OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
 *     MapEffect(Unit) { olaMap ->
 *         olaMap.showCurrentLocation()
 *     }
 * }
 * ```
 */
@Composable
@OlaMapComposable
fun MapEffect(
    vararg keys: Any?,
    block: (SdkOlaMap) -> Unit,
) {
    val effectKeys = keys.toList()

    ComposeNode<MapEffectNode, MapApplier>(
        factory = {
            MapEffectNode(block = block)
        },
        update = {
            set(effectKeys) {
                runBlock()
            }
            update(block) {
                this.block = it
                runBlock()
            }
        },
    )
}

internal class MapEffectNode(
    var block: (SdkOlaMap) -> Unit,
) : MapNode() {
    private var map: SdkOlaMap? = null

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        runBlock()
    }

    override fun onRemoved() {
        map = null
    }

    fun runBlock() {
        map?.let(block)
    }
}
