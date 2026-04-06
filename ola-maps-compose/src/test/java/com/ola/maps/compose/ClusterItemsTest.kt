package com.ola.maps.compose

import com.ola.mapsdk.model.OlaLatLng
import org.junit.Assert.assertTrue
import org.junit.Test

class ClusterItemsTest {
    @Test
    fun `typed cluster points serialize to geojson feature collection`() {
        val geoJson = listOf(
            ClusterPoint(
                id = "ola-campus",
                position = OlaLatLng(12.931423492103944, 77.61648476788898, 0.0),
                properties = mapOf(
                    "title" to "Ola Campus",
                    "count" to 2,
                    "visible" to true,
                ),
            ),
        ).toGeoJson()

        assertTrue(geoJson.contains("\"type\":\"FeatureCollection\""))
        assertTrue(geoJson.contains("\"id\":\"ola-campus\""))
        assertTrue(geoJson.contains("\"title\":\"Ola Campus\""))
        assertTrue(geoJson.contains("\"count\":2"))
        assertTrue(geoJson.contains("\"visible\":true"))
        assertTrue(geoJson.contains("77.61648476788898"))
        assertTrue(geoJson.contains("12.931423492103944"))
    }
}
