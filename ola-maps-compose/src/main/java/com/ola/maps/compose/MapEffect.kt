package com.ola.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import com.ola.mapsdk.view.OlaMap as SdkOlaMap

@Composable
@OlaMapComposable
fun MapEffect(
    key1: Any? = Unit,
    block: (SdkOlaMap) -> Unit,
) {
    ComposeNode<MapEffectNode, MapApplier>(
        factory = {
            MapEffectNode(block = block)
        },
        update = {
            set(key1) {
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
