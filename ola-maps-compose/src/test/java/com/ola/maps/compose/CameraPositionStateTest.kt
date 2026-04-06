package com.ola.maps.compose

import com.ola.mapsdk.camera.OlaCameraPosition
import com.ola.mapsdk.model.OlaLatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CameraPositionStateTest {
    @Test
    fun `save and restore camera position preserves values`() {
        val original = OlaCameraPosition.Builder()
            .setTarget(OlaLatLng(12.931423492103944, 77.61648476788898, 5.0))
            .setBearing(45.0)
            .setTilt(30.0)
            .setZoomLevel(14.5)
            .setDuration(1200)
            .setPaddingStart(1)
            .setPaddingTop(2)
            .setPaddingEnd(3)
            .setPaddingBottom(4)
            .build()

        val restored = restoreOlaCameraPosition(saveOlaCameraPosition(original))
        val actual = requireNotNull(restored)
        val originalTarget = requireNotNull(original.target)
        val actualTarget = requireNotNull(actual.target)

        assertNotNull(restored)
        assertEquals(originalTarget.latitude, actualTarget.latitude, 0.0)
        assertEquals(originalTarget.longitude, actualTarget.longitude, 0.0)
        assertEquals(originalTarget.altitude, actualTarget.altitude, 0.0)
        assertEquals(original.bearing, actual.bearing, 0.0)
        assertEquals(original.tilt, actual.tilt, 0.0)
        assertEquals(original.zoomLevel, actual.zoomLevel, 0.0)
        assertEquals(original.duration, actual.duration)
        assertEquals(original.paddingStart, actual.paddingStart)
        assertEquals(original.paddingTop, actual.paddingTop)
        assertEquals(original.paddingEnd, actual.paddingEnd)
        assertEquals(original.paddingBottom, actual.paddingBottom)
    }

    @Test
    fun `restore camera position returns null for incomplete values`() {
        val restored = restoreOlaCameraPosition(listOf(12.0, 77.0))

        assertNull(restored)
    }
}
