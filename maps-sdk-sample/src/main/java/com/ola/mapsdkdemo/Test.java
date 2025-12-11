package com.ola.mapsdkdemo;

import org.maplibre.android.maps.MapView;
import com.ola.mapsdk.camera.MapControlSettings;
import com.ola.mapsdk.interfaces.IMarker;
import com.ola.mapsdk.interfaces.OlaMapCallback;
import com.ola.mapsdk.model.OlaLatLng;
import com.ola.mapsdk.model.OlaMarkerOptions;
import com.ola.mapsdk.view.OlaMap;

public class Test {
    public static void main(String[] args) {
        com.ola.mapsdk.view.OlaMapView mapView = null;

        mapView.getMap("<API KEY>", new OlaMapCallback() {
                    @Override
                    public void onMapReady(OlaMap olaMap) {
                        // Create a marker
                        OlaMarkerOptions markerOptions = new OlaMarkerOptions.Builder()
                                .setMarkerId("test_marker")
                                .setPosition(new OlaLatLng(12.9716, 77.5946, 0.0))
                                .setIconSize(1.0f)
                                .build();

                        IMarker marker = olaMap.addMarker(markerOptions);

                        // Java-friendly individual property updates (no need to pass all 8 parameters!)
                        marker.updateIconSize(1.5f);                    // Update only size
                        marker.updateIconRotation(45.0f);               // Update only rotation
                        marker.updateSnippet("Updated from Java!");     // Update only snippet
                        marker.updateIconIntRes(com.ola.mapsdkdemo.R.drawable.ic_car_top); // Update only icon resource

                        // Update multiple properties using OlaMarkerOptions
                        OlaMarkerOptions updateOptions = new OlaMarkerOptions.Builder()
                                .setMarkerId("update_marker")
                                .setPosition(new OlaLatLng(12.9716, 77.5946, 0.0))
                                .setIconSize(2.0f)
                                .setIconRotation(90.0f)
                                .setSnippet("Updated via OlaMarkerOptions")
                                .build();

                        marker.updateMarker(updateOptions);

                        // Example: Remove existing bitmap by setting it to null
                        OlaMarkerOptions removeIconOptions = new OlaMarkerOptions.Builder()
                                .setMarkerId("remove_icon_marker")
                                .setPosition(new OlaLatLng(12.9716, 77.5946, 0.0))
                                .setIconBitmap(null)        // This will remove existing bitmap
                                .setIconIntRes(null)        // This will remove existing icon resource
                                .setIconAnchor(null)        // This will remove existing anchor setting
                                .setSnippet(null)           // This will remove existing snippet
                                .setSubSnippet(null)        // This will remove existing sub-snippet
                                .build();

                        marker.updateMarker(removeIconOptions);
                    }

                    @Override
                    public void onMapError(String error) {
                        // Handle map error
                    }
                }, new MapControlSettings.Builder().build()
        );

    }
}