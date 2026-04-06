package com.ola.maps.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.ComposableTargetMarker
import com.ola.mapsdk.interfaces.MarkerEventListener
import com.ola.mapsdk.view.OlaMap

@DslMarker
annotation class OlaMapScopeMarker

@ComposableTargetMarker(description = "Ola Map composable")
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
)
annotation class OlaMapComposable

internal class MapNodeContext(
    val map: OlaMap,
    private val markerClickListeners: MutableMap<String, () -> Unit>,
) {
    fun registerMarkerClickListener(
        markerId: String,
        onClick: (() -> Unit)?,
    ) {
        if (onClick == null) {
            markerClickListeners.remove(markerId)
        } else {
            markerClickListeners[markerId] = onClick
        }
    }

    fun unregisterMarkerClickListener(markerId: String) {
        markerClickListeners.remove(markerId)
    }
}

internal sealed class MapNode {
    open fun onAttached(context: MapNodeContext) = Unit

    open fun onRemoved() = Unit
}

internal class MapRootNode(
    private val map: OlaMap,
) : MapNode() {
    private val markerClickListeners = mutableMapOf<String, () -> Unit>()
    private val context = MapNodeContext(map, markerClickListeners)
    private val children = mutableListOf<MapNode>()

    init {
        map.setMarkerListener(object : MarkerEventListener {
            override fun onMarkerClicked(markerId: String) {
                markerClickListeners[markerId]?.invoke()
            }
        })
    }

    fun addNode(index: Int, node: MapNode) {
        children.add(index, node)
        node.onAttached(context)
    }

    fun moveNodes(from: Int, to: Int, count: Int) {
        if (from == to || count <= 0) return
        val moved = mutableListOf<MapNode>()
        repeat(count) {
            moved += children.removeAt(from)
        }
        val targetIndex = if (from < to) to - count else to
        children.addAll(targetIndex, moved)
    }

    fun removeNodes(index: Int, count: Int) {
        repeat(count) {
            children.removeAt(index).onRemoved()
        }
    }

    override fun onRemoved() {
        children.toList().forEach(MapNode::onRemoved)
        children.clear()
    }
}

internal class MapApplier(
    map: OlaMap,
) : AbstractApplier<MapNode>(MapRootNode(map)) {
    private val rootNode: MapRootNode
        get() = root as MapRootNode

    override fun insertTopDown(index: Int, instance: MapNode) = Unit

    override fun insertBottomUp(index: Int, instance: MapNode) {
        rootNode.addNode(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        rootNode.removeNodes(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        rootNode.moveNodes(from, to, count)
    }

    override fun onClear() {
        rootNode.onRemoved()
    }
}
