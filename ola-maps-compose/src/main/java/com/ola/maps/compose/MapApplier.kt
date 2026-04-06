package com.ola.maps.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.ComposableTargetMarker
import com.ola.mapsdk.view.OlaMap

@DslMarker
annotation class OlaMapScopeMarker

@ComposableTargetMarker(description = "Ola Map composable")
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
)
annotation class OlaMapComposable

internal sealed class MapNode {
    open fun onAttached(map: OlaMap) = Unit

    open fun onRemoved() = Unit
}

internal class MapRootNode(
    private val map: OlaMap,
) : MapNode() {
    private val children = mutableListOf<MapNode>()

    fun addNode(index: Int, node: MapNode) {
        children.add(index, node)
        node.onAttached(map)
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
