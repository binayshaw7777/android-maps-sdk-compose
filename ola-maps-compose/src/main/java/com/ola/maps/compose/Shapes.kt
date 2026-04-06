package com.ola.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ola.mapsdk.model.BezierCurveOptions
import com.ola.mapsdk.model.BorderOptions
import com.ola.mapsdk.model.OlaCircleOptions
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaPolygonOptions
import com.ola.mapsdk.model.OlaPolylineOptions
import com.ola.mapsdk.model.PolygonHolesOptions
import com.ola.mapsdk.view.BezierCurve as SdkBezierCurve
import com.ola.mapsdk.view.Circle as SdkCircle
import com.ola.mapsdk.view.OlaMap as SdkOlaMap
import com.ola.mapsdk.view.Polygon as SdkPolygon
import com.ola.mapsdk.view.Polyline as SdkPolyline
import java.util.Locale

/**
 * Compose-friendly abstraction for line patterns used by polylines, polygon borders,
 * and bezier curves.
 */
@Immutable
enum class StrokePattern(
    internal val sdkValue: String,
) {
    Solid("LINE_SOLID"),
    Dotted("LINE_DOTTED"),
}

/**
 * Compose-friendly border configuration for shapes that support stroke styling.
 *
 * Example:
 * ```kotlin
 * val border = Border(
 *     color = Color.Blue,
 *     width = 2f,
 *     pattern = StrokePattern.Dotted,
 *     dashArray = floatArrayOf(2f, 2f),
 * )
 * ```
 */
@Immutable
data class Border(
    val color: Color = Color.Black,
    val width: Float = 1f,
    val pattern: StrokePattern = StrokePattern.Solid,
    val dashArray: FloatArray? = null,
)

/**
 * Adds a polyline overlay to [OlaMap].
 *
 * Example:
 * ```kotlin
 * Polyline(
 *     points = listOf(
 *         OlaLatLng(12.931423492103944, 77.61648476788898),
 *         OlaLatLng(12.931758797710456, 77.61436504365439),
 *     ),
 *     color = Color.Red,
 *     width = 6f,
 * )
 * ```
 */
@Composable
@OlaMapComposable
fun Polyline(
    points: List<OlaLatLng>,
    color: Color = Color.Black,
    width: Float = 5f,
    pattern: StrokePattern = StrokePattern.Solid,
    dashArray: FloatArray? = null,
) {
    ComposeNode<PolylineNode, MapApplier>(
        factory = {
            PolylineNode(
                points = points,
                color = color,
                width = width,
                pattern = pattern,
                dashArray = dashArray,
            )
        },
        update = {
            set(points) { this.points = it }
            set(color) { this.color = it }
            set(width) { this.width = it }
            set(pattern) { this.pattern = it }
            set(dashArray?.toList()) { this.dashArray = dashArray }
        },
    )
}

/**
 * Adds a polygon overlay to [OlaMap].
 *
 * Example:
 * ```kotlin
 * Polygon(
 *     points = listOf(
 *         OlaLatLng(12.970467915225672, 77.58815217917578),
 *         OlaLatLng(12.962675151452494, 77.57848953729078),
 *         OlaLatLng(12.964623365273798, 77.59560740718187),
 *     ),
 *     fillColor = Color(0x442563EB),
 *     border = Border(color = Color(0xFF2563EB), width = 2f),
 * )
 * ```
 */
@Composable
@OlaMapComposable
fun Polygon(
    points: List<OlaLatLng>,
    fillColor: Color = Color.Black.copy(alpha = 0.2f),
    opacity: Float = fillColor.alpha,
    border: Border? = null,
    hole: List<OlaLatLng> = emptyList(),
) {
    ComposeNode<PolygonNode, MapApplier>(
        factory = {
            PolygonNode(
                points = points,
                fillColor = fillColor,
                opacity = opacity,
                border = border,
                hole = hole,
            )
        },
        update = {
            set(points) { this.points = it }
            set(fillColor) { this.fillColor = it }
            set(opacity) { this.opacity = it }
            set(border) { this.border = it }
            set(hole) { this.hole = it }
        },
    )
}

/**
 * Adds a circle overlay to [OlaMap].
 *
 * Example:
 * ```kotlin
 * Circle(
 *     center = OlaLatLng(12.931423492103944, 77.61648476788898),
 *     radius = 120f,
 *     fillColor = Color(0x44DC2626),
 *     border = Border(color = Color.Red, width = 2f),
 * )
 * ```
 */
@Composable
@OlaMapComposable
fun Circle(
    center: OlaLatLng,
    radius: Float,
    fillColor: Color = Color.Black.copy(alpha = 0.2f),
    opacity: Float = fillColor.alpha,
    blur: Float = 0f,
    border: Border? = null,
) {
    ComposeNode<CircleNode, MapApplier>(
        factory = {
            CircleNode(
                center = center,
                radius = radius,
                fillColor = fillColor,
                opacity = opacity,
                blur = blur,
                border = border,
            )
        },
        update = {
            set(center) { this.center = it }
            set(radius) { this.radius = it }
            set(fillColor) { this.fillColor = it }
            set(opacity) { this.opacity = it }
            set(blur) { this.blur = it }
            set(border) { this.border = it }
        },
    )
}

/**
 * Adds a bezier curve overlay to [OlaMap].
 *
 * Example:
 * ```kotlin
 * BezierCurve(
 *     start = OlaLatLng(12.931423492103944, 77.61648476788898),
 *     end = OlaLatLng(12.931758797710456, 77.61436504365439),
 *     color = Color(0xFFF97316),
 *     etaMessage = "12 min",
 * )
 * ```
 */
@Composable
@OlaMapComposable
fun BezierCurve(
    start: OlaLatLng,
    end: OlaLatLng,
    color: Color = Color.Black,
    width: Float = 5f,
    pattern: StrokePattern = StrokePattern.Solid,
    dashArray: FloatArray? = null,
    curveFactor: Float = 0.5f,
    etaMessage: String? = null,
    etaBackgroundColor: Color? = null,
    etaTextColor: Color? = null,
) {
    ComposeNode<BezierCurveNode, MapApplier>(
        factory = {
            BezierCurveNode(
                start = start,
                end = end,
                color = color,
                width = width,
                pattern = pattern,
                dashArray = dashArray,
                curveFactor = curveFactor,
                etaMessage = etaMessage,
                etaBackgroundColor = etaBackgroundColor,
                etaTextColor = etaTextColor,
            )
        },
        update = {
            set(start) { this.start = it }
            set(end) { this.end = it }
            set(color) { this.color = it }
            set(width) { this.width = it }
            set(pattern) { this.pattern = it }
            set(dashArray?.toList()) { this.dashArray = dashArray }
            set(curveFactor) { this.curveFactor = it }
            set(etaMessage) { this.etaMessage = it }
            set(etaBackgroundColor) { this.etaBackgroundColor = it }
            set(etaTextColor) { this.etaTextColor = it }
        },
    )
}

internal class PolylineNode(
    points: List<OlaLatLng>,
    color: Color,
    width: Float,
    pattern: StrokePattern,
    dashArray: FloatArray?,
) : MapNode() {
    var points: List<OlaLatLng> = points
        set(value) {
            field = value
            polyline?.setPoints(ArrayList(value))
        }

    var color: Color = color
        set(value) {
            field = value
            polyline?.setColor(value.toHexString())
        }

    var width: Float = width
        set(value) {
            field = value
            polyline?.setWidth(value)
        }

    var pattern: StrokePattern = pattern
        set(value) {
            field = value
            polyline?.setLineType(value.sdkValue)
        }

    var dashArray: FloatArray? = dashArray
        set(value) {
            field = value
            recreate()
        }

    private var map: SdkOlaMap? = null
    private var polyline: SdkPolyline? = null
    private val polylineId = "compose-polyline-${nextShapeId()}"

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        polyline = context.map.addPolyline(buildOptions())
    }

    override fun onRemoved() {
        polyline?.removePolyline()
        polyline = null
        map = null
    }

    private fun recreate() {
        val sdkMap = map ?: return
        polyline?.removePolyline()
        polyline = sdkMap.addPolyline(buildOptions())
    }

    private fun buildOptions(): OlaPolylineOptions =
        OlaPolylineOptions.Builder()
            .setPolylineId(polylineId)
            .setPoints(ArrayList(points))
            .setColor(color.toHexString())
            .setWidth(width)
            .setLineType(pattern.sdkValue)
            .apply {
                if (dashArray != null) {
                    setLineDashArray(dashArray!!.toTypedArray())
                }
            }
            .build()
}

internal class PolygonNode(
    points: List<OlaLatLng>,
    fillColor: Color,
    opacity: Float,
    border: Border?,
    hole: List<OlaLatLng>,
) : MapNode() {
    var points: List<OlaLatLng> = points
        set(value) {
            field = value
            polygon?.setPoints(ArrayList(value))
        }

    var fillColor: Color = fillColor
        set(value) {
            field = value
            polygon?.setColor(value.toHexString())
        }

    var opacity: Float = opacity
        set(value) {
            field = value
            recreate()
        }

    var border: Border? = border
        set(value) {
            field = value
            if (value != null) {
                polygon?.setBorderOptions(value.toSdkBorderOptions())
            } else {
                recreate()
            }
        }

    var hole: List<OlaLatLng> = hole
        set(value) {
            field = value
            recreate()
        }

    private var map: SdkOlaMap? = null
    private var polygon: SdkPolygon? = null
    private val polygonId = "compose-polygon-${nextShapeId()}"

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        polygon = context.map.addPolygon(buildOptions())
    }

    override fun onRemoved() {
        polygon?.removePolygon()
        polygon = null
        map = null
    }

    private fun recreate() {
        val sdkMap = map ?: return
        polygon?.removePolygon()
        polygon = sdkMap.addPolygon(buildOptions())
    }

    private fun buildOptions(): OlaPolygonOptions =
        OlaPolygonOptions.Builder()
            .setPolygonId(polygonId)
            .setPoints(ArrayList(points))
            .setColor(fillColor.toHexString())
            .setOpacity(opacity)
            .apply {
                if (border != null) {
                    setBorderOptions(border!!.toSdkBorderOptions())
                }
                if (hole.isNotEmpty()) {
                    setPolygonHolesOptions(
                        PolygonHolesOptions.Builder()
                            .setPoints(ArrayList(hole))
                            .build(),
                    )
                }
            }
            .build()
}

internal class CircleNode(
    center: OlaLatLng,
    radius: Float,
    fillColor: Color,
    opacity: Float,
    blur: Float,
    border: Border?,
) : MapNode() {
    var center: OlaLatLng = center
        set(value) {
            field = value
            circle?.setCenter(value)
        }

    var radius: Float = radius
        set(value) {
            field = value
            circle?.setRadius(value)
        }

    var fillColor: Color = fillColor
        set(value) {
            field = value
            circle?.setColor(value.toHexString())
        }

    var opacity: Float = opacity
        set(value) {
            field = value
            circle?.setOpacity(value)
        }

    var blur: Float = blur
        set(value) {
            field = value
            circle?.setBlur(value)
        }

    var border: Border? = border
        set(value) {
            field = value
            if (value != null) {
                circle?.setBorderOptions(value.toSdkBorderOptions())
            } else {
                recreate()
            }
        }

    private var map: SdkOlaMap? = null
    private var circle: SdkCircle? = null
    private val circleId = nextShapeId().toLong()

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        circle = context.map.addCircle(buildOptions())
    }

    override fun onRemoved() {
        circle?.removeCircle()
        circle = null
        map = null
    }

    private fun recreate() {
        val sdkMap = map ?: return
        circle?.removeCircle()
        circle = sdkMap.addCircle(buildOptions())
    }

    private fun buildOptions(): OlaCircleOptions =
        OlaCircleOptions.Builder()
            .setCircleId(circleId)
            .setOlaLatLng(center)
            .setRadius(radius)
            .setColorHexCode(fillColor.toHexString())
            .setCircleOpacity(opacity)
            .setCircleBlur(blur)
            .apply {
                if (border != null) {
                    setBorderOptions(border!!.toSdkBorderOptions())
                }
            }
            .build()
}

internal class BezierCurveNode(
    start: OlaLatLng,
    end: OlaLatLng,
    color: Color,
    width: Float,
    pattern: StrokePattern,
    dashArray: FloatArray?,
    curveFactor: Float,
    etaMessage: String?,
    etaBackgroundColor: Color?,
    etaTextColor: Color?,
) : MapNode() {
    var start: OlaLatLng = start
        set(value) {
            field = value
            curve?.setPoints(value, end)
        }

    var end: OlaLatLng = end
        set(value) {
            field = value
            curve?.setPoints(start, value)
        }

    var color: Color = color
        set(value) {
            field = value
            curve?.setColor(value.toHexString())
        }

    var width: Float = width
        set(value) {
            field = value
            curve?.setWidth(value)
        }

    var pattern: StrokePattern = pattern
        set(value) {
            field = value
            curve?.setLineType(value.sdkValue)
        }

    var dashArray: FloatArray? = dashArray
        set(value) {
            field = value
            recreate()
        }

    var curveFactor: Float = curveFactor
        set(value) {
            field = value
            recreate()
        }

    var etaMessage: String? = etaMessage
        set(value) {
            field = value
            recreate()
        }

    var etaBackgroundColor: Color? = etaBackgroundColor
        set(value) {
            field = value
            recreate()
        }

    var etaTextColor: Color? = etaTextColor
        set(value) {
            field = value
            recreate()
        }

    private var map: SdkOlaMap? = null
    private var curve: SdkBezierCurve? = null
    private val curveId = "compose-bezier-${nextShapeId()}"

    override fun onAttached(context: MapNodeContext) {
        map = context.map
        curve = context.map.addBezierCurve(buildOptions())
    }

    override fun onRemoved() {
        curve?.removeBezierCurve()
        curve = null
        map = null
    }

    private fun recreate() {
        val sdkMap = map ?: return
        curve?.removeBezierCurve()
        curve = sdkMap.addBezierCurve(buildOptions())
    }

    private fun buildOptions(): BezierCurveOptions =
        BezierCurveOptions.Builder()
            .setCurveId(curveId)
            .setStartPoint(start)
            .setEndPoint(end)
            .setColor(color.toHexString())
            .setWidth(width)
            .setLineType(pattern.sdkValue)
            .setCurveFactor(curveFactor)
            .apply {
                if (dashArray != null) {
                    setLineDashArray(dashArray!!.toTypedArray())
                }
                this@BezierCurveNode.etaMessage?.let { message ->
                    setEtaMessage(message)
                }
                this@BezierCurveNode.etaBackgroundColor?.let { backgroundColor ->
                    setEtaBgColor(backgroundColor.toHexString())
                }
                this@BezierCurveNode.etaTextColor?.let { textColor ->
                    setEtaTextColor(textColor.toHexString())
                }
            }
            .build()
}

internal fun Color.toHexString(): String =
    String.format(Locale.US, "#%08X", toArgb())

internal fun Border.toSdkBorderOptions(): BorderOptions =
    BorderOptions.Builder()
        .setBorderColor(color.toHexString())
        .setBorderWidth(width)
        .setBorderLineType(pattern.sdkValue)
        .apply {
            if (dashArray != null) {
                setBorderLineDashArray(dashArray.toTypedArray())
            }
        }
        .build()

private var shapeIdCounter = 0

private fun nextShapeId(): Int {
    shapeIdCounter += 1
    return shapeIdCounter
}
