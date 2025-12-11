# Ola Maps — Map SDK Android 

Ola Map Android SDK offers a comprehensive solution for integrating interactive map functionalities into your application.
By utilizing the Maps SDK for Android, you can incorporate maps powered by Ola's data directly into your app.

## Setting Up the SDK

### 1. Download SDK
Download the Android Map SDK and copy the `OlaMapSdk.aar` file into your app’s `libs` folder.

### 2. Add SDK Dependency
Include the SDK in your project by adding the following dependencies to your build.gradle

```gradle
//OlaMap SDK
implementation(files("libs/OlaMapSdk-1.8.4.aar"))

//Maplibre
implementation ("org.maplibre.gl:android-sdk:11.13.1")
implementation ("org.maplibre.gl:android-plugin-annotation-v9:3.0.2")
implementation ("org.maplibre.gl:android-plugin-markerview-v9:3.0.2")
```

### 3. Define Map View in XML
Include below code in your xml file where you want to load a map. 

```xml
<com.ola.maps.OlaMapView
    android:id="@+id/mapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

### 4. Map view initialization
Initialize the map in your activity or fragment:

```kotlin
mapView = findViewById(R.id.mapView)

mapView.getMap(apiKey = "<API KEY>",
  olaMapCallback = object : OlaMapCallback {
      override fun onMapReady(olaMap: OlaMap) {
          // Map is ready to use
      }

      override fun onMapError(error: String) {
          // Handle map error
      }
  }, mapControlSettings = MapControlSettings.Builder().build()
)
```

## Features
1. Dynamic maps
2. Markers
3. Infowindows
4. Polyline
5. Circle
6. Polygon
7. Bezier Curve
8. Events and Methods
9. Controls and Gestures
10. Marker Clustering


### 1. Dynamic Map
The Dynamic Map provides a comprehensive solution for integrating interactive map functionalities into your application.
SDK supports dynamic maps with various interactive features:
- Scroll
- Zoom
- Rotate
- Tilt
- Tracking users current location

### 2. Markers
Easily add markers to your map to highlight important locations or points of interest, customize marker icons and styles for a visually appealing representation of data. 

#### 1. Adding a Marker 
To add a marker to the map, you need to create an OlaMarkerOptions object with the desired properties and then use the addMarker method.

```kotlin
val markerOptions1 = OlaMarkerOptions.Builder()
        .setMarkerId("marker1")
        .setPosition(OlaLatLng(18.52145653681468, 73.93178277572254))
        .setIsIconClickable(true)
        .setIconRotation(0f)
        .setIsAnimationEnable(true)
        .setIsInfoWindowDismissOnClick(true)
        .build()

marker1 = olaMap.addMarker(markerOptions1)
```
- setMarkerId(String id): Sets a unique identifier for the marker.
- setPosition(OlaLatLng position): Sets the position of the marker.
- setIsIconClickable(Boolean isClickable): Enables or disables the marker's icon clickability.
- setIconRotation(Float rotation): Sets the rotation angle of the marker's icon.
- setIsAnimationEnable(Boolean isAnimationEnable): Enables or disables animations for the marker.
- setIsInfoWindowDismissOnClick(Boolean isInfoWindowDismissOnClick): Sets whether the info window should be dismissed when clicked.

#### 2. Removing a Marker
To remove a marker from the map, use the removeMarker method on the marker object.
```kotlin
marker1.removeMarker()
```

#### 3. Updating a Marker
To update the an existing marker, use the appropriate method on the marker object.
```kotlin
marker1.updateMarker(
    position: OlaLatLng?,
    iconAnchor = "top-left",
    iconBitmap = customBitmap,
    iconIntRes = R.drawable.custom_icon,
    iconOffset = floatArrayOf(10f, 20f),
    iconRotation = 45f,
    iconSize = 1.5f,
    snippet = "Updated snippet text",
    subSnippet = "Updated sub-snippet text"
)
```

### 3. InfoWindows
Display informative pop-up windows when users interact with markers or specific map areas. Infowindows can contain descriptive text and extra details enhancing user engagement.

#### 1. Adding an Info Window
To add an info window to a marker, you need to create an OlaMarkerOptions object with the desired properties, including the snippet for the info window, and then use the addMarker method.
```kotlin
val markerOptions1 = OlaMarkerOptions.Builder()
          .setMarkerId("1232")
          .setPosition(olaCampus)
          .setSnippet("This is the infowindow")
          .setIsIconClickable(true)
          .setIconRotation(0f)
          .setIsAnimationEnable(true)
          .setIsInfoWindowDismissOnClick(true)
          .build()

marker1 = olaMap.addMarker(markerOptions1)
```
- setMarkerId(String id): Sets a unique identifier for the marker.
- setPosition(OlaLatLng position): Sets the position of the marker.
- setSnippet(String snippet): Sets the text to be displayed in the info window.
- setIsIconClickable(Boolean isClickable): Enables or disables the marker's icon clickability.
- setIconRotation(Float rotation): Sets the rotation angle of the marker's icon.
- setIsAnimationEnable(Boolean isAnimationEnable): Enables or disables animations for the marker.
- setIsInfoWindowDismissOnClick(Boolean isInfoWindowDismissOnClick): Sets whether the info window should be dismissed when clicked.

#### 2. Hiding an Info Window
To hide an info window from a marker, use the hideInfoWindow method on the marker object.
```kotlin
marker1.hideInfoWindow()
```
#### 3. Updating an Info Window
To update the text in an existing info window, use the updateInfoWindow method on the marker object.
```kotlin
marker1.updateInfoWindow("This is an updated info window")
```

### 4. Polyline
Draw polylines to represent paths or routes on the map, ideal for navigation or tracking purposes. Customize polyline styles, including color and thickness, to enhance visual clarity.

#### 1. Adding a Polyline
To add a polyline to the map, you need to create an OlaPolylineOptions object with the desired properties and then use the addPolyline method.
```kotlin
val points = arrayListOf(OlaLatLng(12.931423492103944, 77.61648476788898),
             OlaLatLng(12.931758797710456, 77.61436504365439))

val polylineOptions = OlaPolylineOptions.Builder()
   .setPolylineId("pid1")
   .setPoints(points)
   .build()

polyline1 = olaMap.addPolyline(polylineOptions)
```
- setPolylineId(String id): Sets a unique identifier for the polyline.
- setPoints(ArrayList<OlaLatLng> points): Sets the points that define the vertices of the polyline.
- setColor(String color): Sets the color of the polyline.
- setLineType(String lineType): Sets the line type of the polyline.
- setWidth(Float width): Sets the width of the polyline.

#### 2. Removing a Polyline
To remove a polyline from the map, use the removePolyline method on the polyline object.
```kotlin
polyline.removePolyline()
```
#### 3. Updating Polyline
To update other properties of an existing polyline, use the respective methods on the polyline object.
```kotlin
val newPoints = arrayListOf(
     OlaLatLng(12.931423492103944, 77.61648476788898),
     OlaLatLng(12.931758797710456, 77.61436504365439),
     OlaLatLng(12.932123492103944, 77.61748476788898)
 )
 polyline.setPoints(newPoints)
```

### 5. Circle
Easily draw circles on the map around specific points, useful for showcasing coverage areas or proximity. Adjust the circle's radius and style to fit your application’s needs.

#### 1. Adding a Circle
To add a circle to the map, you need to create an OlaCircleOptions object with the desired properties and then use the addCircle method.
```kotlin
val olaCampus = OlaLatLng(12.931423492103944, 77.61648476788898)
  val circleOptions = OlaCircleOptions.Builder()
      .setOlaLatLng(olaCampus)
      .setRadius(100f)
      .build()

  circle = olaMap.addCircle(circleOptions)
```
- setOlaLatLng(OlaLatLng position): Sets the position of the circle's center.
- setCenter(OlaLatLng center): Sets the center of the circle.
- setRadius(Float radius): Sets the radius of the circle in meters.
- setColor(String color): Sets the color of the circle.
- setBlur(Float blur): Sets the blur radius of the circle.
- setBorderOptions(BorderOptions borderOptions): Sets the border options of the circle.
- setOpacity(opacity: Float): Sets the opacity of the circle.

#### 2. Removing a Circle
To remove a circle from the map, use the removeCircle method on the circle object.
```kotlin
circle.removeCircle()
```

#### 3. Updating a Circle
To update other properties of an existing circle, use the respective methods on the circle object.
```kotlin
circle.setColor("#FF0000")
```

### 6. Polygon
Create multi-sided shapes to represent areas or boundaries on the map, perfect for zoning or highlighting regions of interest. Customize polygon fill colors and borders to enhance visibility and distinction.

#### 1. Adding a Polygon
To add a polygon to the map, you need to create an OlaPolygonOptions object with the desired properties and then use the addPolygon method.
```kotlin
val points = arrayListOf(
       OlaLatLng(18.56892987516166, 73.88081911869274),
       OlaLatLng(18.58960286647498, 73.83615669644608)
   )
val polygonOptions = OlaPolygonOptions.Builder()
       .setPolygonId("polygon1")
       .setPoints(points)
       .build()

polygon = olaMap.addPolygon(polygonOptions)
```
- setPolygonId(String id): Sets a unique identifier for the polygon.
- setPoints(ArrayList<OlaLatLng> points): Sets the points that define the vertices of the polygon.
- setColor(color: String): Sets the color of the polygon.
- setBorderOptions(borderOptions: BorderOptions): Sets the border options of the polygon.

#### 2. Removing a Polygon
To remove a polygon from the map, use the removePolygon method on the polygon object.
```kotlin
polygon.removePolygon()
```

#### 3. Updating a Polygon
To update other properties of an existing polygon, use the respective methods on the polygon object.
```kotlin
val newPoints = arrayListOf(
        OlaLatLng(18.56892987516166, 73.88081911869274),
        OlaLatLng(18.58960286647498, 73.83615669644608),
        OlaLatLng(18.59060286647498, 73.83615669644608)
    )
polygon.setPoints(newPoints)
```
```kotlin
polygon.setColor("#FF0000")
```

### 7. Bezier Curve
Implement smooth, curved lines between points on the map, offering a visually appealing way to represent routes or connections. Customize the control points to shape the curve and achieve the desired aesthetic.

#### 1. Adding a Bezier Curve
To add a Bezier curve to the map, you need to create a BezierCurveOptions object with the desired properties and then use the addBezierCurve method.
```kotlin
val startPoint = OlaLatLng(12.931423492103944, 77.61648476788898)
val endPoint = OlaLatLng(12.931758797710456, 77.61436504365439)

val bezierCurveOptions = BezierCurveOptions.Builder()
        .setCurveId("bcurve1")
        .setStartPoint(startPoint)
        .setEndPoint(endPoint)
        .build()

bezierCurve = olaMap.addBezierCurve(bezierCurveOptions)
```
- setCurveId(String id): Sets a unique identifier for the Bezier curve.
- setStartPoint(OlaLatLng startPoint): Sets the starting point of the Bezier curve.
- setEndPoint(OlaLatLng endPoint): Sets the ending point of the Bezier curve.
- setPoints(startPoint: OlaLatLng, endPoint: OlaLatLng): Sets the starting and ending points of the Bezier curve.
- setColor(color: String): Sets the color of the Bezier curve.
- setLineType(lineType: String): Sets the line type of the Bezier curve.
- setWidth(width: Float): Sets the width of the Bezier curve.

#### 2. Removing a Bezier Curve
To remove a Bezier Curve from the map, use the removeBezierCurve method on the Bezier Curve object.
```kotlin
bezierCurve.removeBezierCurve()
```

#### 3. Updating a Bezier Curve
To update other properties of an existing Bezier Curve, use the respective methods on the Bezier Curve object.
```kotlin
val newStartPoint = OlaLatLng(12.932123492103944, 77.61748476788898)
val newEndPoint = OlaLatLng(12.933758797710456, 77.61836504365439)

bezierCurve.setPoints(newStartPoint, newEndPoint)
```
```kotlin
bezierCurve.setLineType(LineType.LINE_SOLID) // or LineType.LINE_DOTTED
```

## 8. Events & Methods
#### 1. Zoom to location
To zoom the map to a specific location, use the zoomToLocation method.
```kotlin
val location = OlaLatLng(12.9715987, 77.594566)
val zoomLevel = 15.0
olaMap.zoomToLocation(location, zoomLevel)
```

#### 2. Get Current Location
To retrieve the user's current location, use the getCurrentLocation method. This method returns an OlaLatLng object representing the current location or null if the location is not available. 
```kotlin
val currentLocation: OlaLatLng? = olaMap?.getCurrentLocation()
if (currentLocation != null) {
    // Use the current location
} else {
    // Handle the case where the location is not available
}
```

#### 3. Show Current Location
To display the user's current location on the map, use the showCurrentLocation method.
```kotlin
olaMap?.showCurrentLocation()
```

#### 4. Hide Current Location
To hide the user's current location from the map, use the hideCurrentLocation method.
```kotlin
olaMap?.hideCurrentLocation()
```

## 9. Controls & Gestures
SDK allows you to customize the map's UI settings through the MapControlSettings object. You can enable or disable the following gestures and controls:
- Rotate Gestures: Enable or disable the ability to rotate the map.
- Scroll Gestures: Enable or disable the ability to scroll the map.
- Zoom Gestures: Enable or disable the ability to zoom the map.
- Tilt Gestures: Enable or disable the ability to tilt the map.
- Double Tap Gestures: Enable or disable the ability to double tap the map.
- Compass: Enable or disable the compass icon on the map.

To customize the UI settings, create a MapControlSettings object with the desired properties and pass it to the getMap method when initializing the map view.
```kotlin
val mapControlSettings = MapControlSettings.Builder()
   .setRotateGesturesEnabled(true)
   .setScrollGesturesEnabled(true)
   .setZoomGesturesEnabled(false)
   .setCompassEnabled(true)
   .setTiltGesturesEnabled(true)
   .setDoubleTapGesturesEnabled(true)
   .build()

mapView.getMap(apiKey = "<API KEY>",
olaMapCallback = object : OlaMapCallback {}, mapControlSettings)
```

## 10. Marker Clustering
SDK allows you to create a marker clustering that helps manage a large number of markers on a map by grouping them into clusters based on their proximity to each other.
As the user zooms out, markers that are close together are combined into a single cluster marker.
When zooming in, these clusters expand to show individual markers.
This feature enhances the map's readability and performance, particularly when dealing with high marker densities.

#### 1. Adding Clustered Markers Using FeatureCollection
To add clustered markers using a FeatureCollection, use the addClusteredMarkers method.
This method takes in an OlaMarkerClusterOptions object and a FeatureCollection.
```kotlin
val featureCollection: FeatureCollection = // Initialize your feature collection here
val clusterOptions = OlaMarkerClusterOptions.Builder()
     .setClusterRadius(50)
     .setDefaultMarkerColor("#FF0000")
     .setDefaultClusterColor("#00FF00")
     .setDefaultMarkerIcon(bitmap)
     .setTextSize(12f)
     .setTextColor("#FFFFFF")
     .build()

val clusteredMarkers = olaMap.addClusteredMarkers(clusterOptions, featureCollection)
```

#### 2. Adding Clustered Markers Using FeatureCollection
To add clustered markers using a GeoJSON string, use the addClusteredMarkers method with the GeoJSON string as a parameter.
```kotlin
val geoJson: String = // Your GeoJSON string here
val clusterOptions = OlaMarkerClusterOptions.Builder()
     .setClusterRadius(50)
     .setDefaultMarkerColor("#FF0000")
     .setDefaultClusterColor("#00FF00")
     .setTextSize(12f)
     .setTextColor("#FFFFFF")
     .build()

val clusteredMarkers = olaMap?.addClusteredMarkers(clusterOptions, geoJson)
```
The OlaMarkerClusterOptions class allows you to customize the behavior and appearance of clustered markers. Below are the key properties you can set:
- clusterRadius: The radius within which markers are grouped into clusters. (e.g., setClusterRadius(50))
- defaultMarkerColor: The color of individual markers when they are not clustered. (e.g., setDefaultMarkerColor("#FF0000"))
- defaultClusterColor: The color of the cluster marker. (e.g., setDefaultClusterColor("#00FF00"))
- defaultMarkerIcon: A custom icon for individual markers. (e.g., setDefaultMarkerIcon(bitmap))
- stop1Color, stop2Color: Stop colors 1 and 2 for the background of the clustered markers when zoomed out and the markers get merged into a cluster
- textSize: The size of the text displayed on cluster markers. (e.g., setTextSize(12f))
- textColor: The color of the text displayed on cluster markers. (e.g., setTextColor("#FFFFFF"))

#### 3. Update Clustered Markers
You can update the existing clustered markers by providing a new FeatureCollection or GeoJSON string.
You can also optionally pass a new OlaMarkerClusterOptions object to change the clustering settings.
```kotlin
//Using FeatureCollection:
clusteredMarkers.updateClusteredMarkers(newFeatureCollection, newClusterOptions)

//Using GeoJSON String:
clusteredMarkers.updateClusteredMarkers(newGeoJsonString, newClusterOptions)
```

#### 4. Remove Clustered Markers
To remove the clustered markers from the map, use the removeClusteredMarkers method.
```kotlin
clusteredMarkers.removeClusteredMarkers()
```
