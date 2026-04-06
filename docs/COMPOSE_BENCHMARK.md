# Compose Maps Benchmark

## Goal

This document tracks how `ola-maps-compose` should evolve relative to the strongest Android map SDK Compose experiences available today.

The benchmark is not about copying every API 1:1. It is about:

- the cleanest Android developer experience
- the least boilerplate for common cases
- stable state and update semantics
- good escape hatches for unsupported features
- polished docs and sample quality

## Snapshot

### Google Maps Compose

Strengths:

- clear `GoogleMap { ... }` content model
- strong state-hoisting patterns like `CameraPositionState` and `MarkerState`
- separate clustering utilities module
- explicit `MapEffect` for raw access
- polished API docs and examples

Backlog implications for Ola:

- keep `OlaMap` child content declarative and stable
- keep state holders explicit and easy to hoist
- separate first-class clustering support from raw escape hatches
- improve docs and examples until common usage feels self-explanatory

Reference:

- https://github.com/googlemaps/android-maps-compose
- https://googlemaps.github.io/android-maps-compose/
- https://googlemaps.github.io/android-maps-compose/maps-compose-utils/com.google.maps.android.compose.clustering/index.html

### Mapbox Compose

Strengths:

- official Compose support
- generated annotation composables for point, polyline, polygon and circle-like overlays
- state-driven annotation APIs
- good low-level access for advanced customization

Backlog implications for Ola:

- continue wrapping overlays as dedicated composables instead of generic builder objects
- consider richer state objects for shapes if update ergonomics matter
- keep the wrapper thin where the SDK already has strong primitives

Reference:

- https://docs.mapbox.com/android/maps/guides/using-jetpack-compose/
- https://docs.mapbox.com/android/maps/api/11.15.2/mapbox-maps-android/com.mapbox.maps.extension.compose.annotation.generated/-polyline-annotation.html

### MapLibre Compose

Strengths:

- Compose-first runtime for layers and data sources
- more styling/layer flexibility than annotation-only APIs
- explicit runtime data-source model

Backlog implications for Ola:

- current Ola wrapper is overlay-first, not style/layer-first
- if Ola ever exposes richer source/layer APIs, a second tier of advanced Compose wrappers may be worth adding
- current short-term priority stays on ergonomic wrappers for existing public OLA SDK features

Reference:

- https://maplibre.org/maplibre-compose/
- https://maplibre.org/maplibre-compose/layers/

### Mappls / MapMyIndia

Observed position:

- strong native Android SDK surface
- good regional relevance
- Compose-first public experience is not as clearly surfaced as Google or Mapbox from the sources reviewed

Backlog implications for Ola:

- Ola has an opportunity to look more modern and more Android-native in Compose than regional competitors
- sample quality and migration docs can become a differentiator, not just API surface

Reference:

- https://developer.mappls.com/documentation/sdk/android/
- https://developer.mappls.com/documentation/sdk/android/docs/v2.0.0/Mappls-Map-SDK/

### TomTom / HERE-style Compose direction

Observed position:

- modern Android map stacks increasingly ship dedicated Compose integration instead of treating Compose as a thin `AndroidView` wrapper
- Compose map display modules are now part of the competitive baseline

Backlog implications for Ola:

- keep building Ola Compose as a first-class module, not a sample-only adapter
- quality bar must include docs, samples and polished public naming, not just feature coverage

Reference:

- https://developer.tomtom.com/navigation/android/getting-started/displaying-your-first-map
- https://developer.tomtom.com/navigation/android/guides/map-display/map-display-for-compose/map-configuration

## Ola Differentiators To Pursue

- best migration path from XML OLA Maps to Compose
- smallest amount of setup for common marker and route scenarios
- strong India-focused developer story with modern Compose ergonomics
- clean sample app that demonstrates parity with the XML demo
- contribution-ready API naming and documentation

## Concrete Backlog

- [ ] Review whether `MapEffect` should support multiple keys and/or suspend behavior
- [ ] Expand `MapProperties` and `MapUiSettings` deliberately instead of mirroring every raw SDK flag
- [ ] Improve clustering ergonomics beyond raw data wrappers
- [ ] Decide whether shape states should be hoisted like marker state
- [ ] Add README sections that mirror the strongest competitor docs: quickstart, overlays, clustering, migration
- [ ] Add screenshots or demo assets once the sample UI is polished
