package com.ola.maps.compose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapSettingsTest {
    @Test
    fun `allGesturesDisabled turns off all gestures and keeps compass configurable`() {
        val settings = MapUiSettings.allGesturesDisabled(compassEnabled = false)

        assertFalse(settings.isRotateGesturesEnabled)
        assertFalse(settings.isScrollGesturesEnabled)
        assertFalse(settings.isZoomGesturesEnabled)
        assertFalse(settings.isCompassEnabled)
        assertFalse(settings.isTiltGesturesEnabled)
        assertFalse(settings.isDoubleTapGesturesEnabled)
    }

    @Test
    fun `allGesturesEnabled matches default settings`() {
        val settings = MapUiSettings.allGesturesEnabled()

        assertTrue(settings.isRotateGesturesEnabled)
        assertTrue(settings.isScrollGesturesEnabled)
        assertTrue(settings.isZoomGesturesEnabled)
        assertTrue(settings.isCompassEnabled)
        assertTrue(settings.isTiltGesturesEnabled)
        assertTrue(settings.isDoubleTapGesturesEnabled)
    }

    @Test
    fun `map properties keep configured location behavior values`() {
        val properties = MapProperties(
            isMyLocationEnabled = true,
            moveCameraToMyLocationOnEnable = true,
            myLocationZoomLevel = 16.5,
            myLocationAnimationDurationMs = 1400,
        )

        assertTrue(properties.isMyLocationEnabled)
        assertTrue(properties.moveCameraToMyLocationOnEnable)
        assertEquals(16.5, properties.myLocationZoomLevel, 0.0)
        assertEquals(1400, properties.myLocationAnimationDurationMs)
    }
}
