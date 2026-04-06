# Ola Maps Compose

`ola-maps-compose` is a Jetpack Compose wrapper around the official Ola Maps Android SDK.

This module is designed to feel similar to modern Compose-first map libraries:

- declarative `OlaMap { ... }` content
- state-hoisted camera and marker APIs
- Compose wrappers for markers, shapes, and clustering
- an escape hatch for raw SDK access through `MapEffect`

## Requirements

This module does not bundle the Ola Maps Android SDK `.aar`.

You must supply the official Ola SDK locally on your own machine.

Example `local.properties`:

```properties
OLA_MAPS_API_KEY=your_api_key_here
OLA_MAPS_SDK_AAR=C:\\path\\to\\OlaMapSdk-1.8.4.aar
```

The sample app and Gradle setup read `OLA_MAPS_SDK_AAR` from:

1. `local.properties`
2. `OLA_MAPS_SDK_AAR` environment variable

## Gradle Setup

Add the Compose wrapper:

```kotlin
implementation(project(":ola-maps-compose"))
implementation(files(providers.gradleProperty("OLA_MAPS_SDK_AAR").orNull ?: System.getenv("OLA_MAPS_SDK_AAR")))
```

You also need the Ola SDK runtime dependencies already used by this repo:

```kotlin
implementation("org.maplibre.gl:android-sdk:11.13.1")
implementation("org.maplibre.gl:android-plugin-annotation-v9:3.0.2")
implementation("org.maplibre.gl:android-plugin-markerview-v9:3.0.2")
```

## Quickstart

```kotlin
val olaCampus = OlaLatLng(12.931423492103944, 77.61648476788898)
val cameraPositionState = rememberCameraPositionState()
val markerState = rememberMarkerState(position = olaCampus)

OlaMap(
    apiKey = BuildConfig.OLA_MAPS_API_KEY,
    cameraPositionState = cameraPositionState,
) {
    Marker(
        state = markerState,
        snippet = "Ola Campus",
        subSnippet = "Compose wrapper",
    )
}
```

## Public API

### Host Map

- `OlaMap`
- `MapProperties`
- `MapUiSettings`
- `MapEffect`

### State

- `CameraPositionState`
- `rememberCameraPositionState`
- `MarkerState`
- `rememberMarkerState`

### Overlays

- `Marker`
- `Polyline`
- `Polygon`
- `Circle`
- `BezierCurve`
- `ClusteredMarkers`

## Usage Patterns

### Camera Control

```kotlin
val cameraPositionState = rememberCameraPositionState()

Button(onClick = {
    cameraPositionState.move(
        target = OlaLatLng(12.931423492103944, 77.61648476788898),
        zoomLevel = 15.0,
        durationMs = 1000,
    )
}) {
    Text("Move Camera")
}
```

### Shapes

```kotlin
OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
    Polyline(
        points = listOf(
            OlaLatLng(12.931423492103944, 77.61648476788898),
            OlaLatLng(12.931758797710456, 77.61436504365439),
        ),
        color = Color.Red,
        width = 6f,
    )
}
```

### Clustering

```kotlin
OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
    ClusteredMarkers(
        items = ClusterItems.GeoJson(geoJsonString),
        options = ClusterOptions(clusterRadius = 40),
    )
}
```

### Raw SDK Escape Hatch

```kotlin
OlaMap(apiKey = BuildConfig.OLA_MAPS_API_KEY) {
    MapEffect(Unit) { olaMap ->
        olaMap.showCurrentLocation()
    }
}
```

## Current Notes

- marker click callbacks are exposed
- info window click support is not yet verified in the public wrapper
- clustering currently wraps GeoJSON and `FeatureCollection` inputs directly
- the sample app is the current smoke-test surface for the module
