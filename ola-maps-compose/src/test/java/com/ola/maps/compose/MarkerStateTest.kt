package com.ola.maps.compose

import com.ola.mapsdk.model.OlaLatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class MarkerStateTest {
    @Test
    fun `save and restore marker position preserves coordinates`() {
        val original = OlaLatLng(12.931423492103944, 77.61648476788898, 8.0)

        val restored = restoreMarkerPosition(saveMarkerPosition(original))

        assertEquals(original.latitude, restored.latitude, 0.0)
        assertEquals(original.longitude, restored.longitude, 0.0)
        assertEquals(original.altitude, restored.altitude, 0.0)
    }
}
