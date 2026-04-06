# Ola Maps Compose Roadmap

## Objective

Ship `ola-maps-compose` as a first-class Compose SDK for OLA Maps with:

- Google-style declarative usage for common map cases
- clean interop with the existing XML SDK surface
- strong sample quality for internal review, upstream contribution, and public showcase

## Current Snapshot

### Done

- [x] Create `ola-maps-compose` library module
- [x] Add `OlaMap` composable host around `OlaMapView`
- [x] Handle map lifecycle inside the Compose library
- [x] Add `CameraPositionState`
- [x] Add `MapProperties` and `MapUiSettings`
- [x] Build declarative map content runtime with map applier / nodes
- [x] Add `MapEffect` raw SDK escape hatch
- [x] Add `Marker` and `MarkerState`
- [x] Add `Polyline`
- [x] Add `Polygon`
- [x] Add `Circle`
- [x] Add `BezierCurve`
- [x] Add `ClusteredMarkers`
- [x] Route marker click callbacks through Compose
- [x] Wire API key from `local.properties`
- [x] Launch sample app into Compose activity by default
- [x] Recreate the XML playground surface in the Compose sample
- [x] Keep compile verification running for `:ola-maps-compose` and `:maps-sdk-sample`

### Open Gaps

- [ ] Expand `MapProperties` only for stable runtime toggles that are safe to recompose
- [ ] Tighten `MapUiSettings` around the most useful XML controls surface
- [ ] Verify whether info window click callbacks exist in the current public AAR
- [ ] Verify whether shape click callbacks are possible in the current public AAR
- [ ] Improve clustering ergonomics beyond raw GeoJSON and `FeatureCollection`
- [ ] Split the parity-heavy Compose sample into polished showcase flows
- [ ] Write XML-to-Compose migration guidance
- [ ] Prepare contribution notes for upstreaming to Ola Maps
- [ ] Raise docs and demos to benchmark-ready quality

## Delivery Checklist

### Milestone 1: Host Runtime

- [x] Keep `AndroidView` limited to hosting `OlaMapView`
- [x] Move lifecycle wiring into the library layer
- [x] Bind map readiness into a child `Composition`
- [x] Keep listener registration separate from overlay child management
- [x] Dispose child composition cleanly when map or composable is removed
- [x] Rebind `CameraPositionState` safely across recomposition
- [x] Expose map initialization failures through `onMapError`

### Milestone 2: Core State

- [x] Add `rememberCameraPositionState(initialPosition = ...)`
- [x] Add `rememberCameraPositionState { ... }`
- [x] Make camera state saveable
- [x] Add `MarkerState`
- [x] Make marker state saveable
- [ ] Audit whether any shape state objects are worth hoisting publicly
- [ ] Decide whether camera animation progress / movement reason needs public surface

### Milestone 3: Marker API

- [x] Support marker position updates from state
- [x] Support snippet / sub-snippet
- [x] Support drawable resource icons
- [x] Support bitmap icons
- [x] Support icon size
- [x] Support icon rotation
- [x] Support icon anchor
- [x] Support icon offset
- [x] Support marker click callbacks
- [ ] Review if marker title API should be added explicitly
- [ ] Review if marker z-index / visibility toggles exist and should be wrapped

### Milestone 4: Shapes API

- [x] Add color-to-hex conversion helper
- [x] Add border abstraction
- [x] Add stroke pattern abstraction
- [x] Add `Polyline`
- [x] Add `Polygon`
- [x] Add `Circle`
- [x] Add `BezierCurve`
- [x] Add sample coverage for all current shapes
- [ ] Audit whether shape updates recreate more often than necessary
- [ ] Audit whether shape clicks are supported in the SDK and should be wrapped

### Milestone 5: Clustering

- [x] Add first-pass `ClusteredMarkers`
- [x] Support GeoJSON input
- [x] Support `FeatureCollection` input
- [x] Support cluster options updates
- [x] Add sample usage for clustering
- [ ] Add a friendlier typed item model beyond raw data blobs
- [ ] Decide whether clustering belongs in a separate optional artifact like Google Maps Compose utils
- [ ] Add docs explaining current clustering limits and tradeoffs

### Milestone 6: Sample App

- [x] Launch Compose sample by default
- [x] Add marker playground actions
- [x] Add polyline / polygon / circle / bezier playground actions
- [x] Add current location actions
- [x] Add UI settings actions
- [x] Add map events panel
- [x] Add clustering actions
- [x] Add info window actions through raw SDK interop where wrapper is incomplete
- [ ] Split one giant playground into smaller showcase-ready flows
- [ ] Add a dedicated quickstart sample that is much smaller than the parity demo
- [ ] Add a migration sample showing XML and Compose side-by-side

### Milestone 7: API Tightening

- [ ] Review all public names against Google Maps Compose patterns
- [ ] Remove any unnecessary string-heavy parameters from public API where wrappers can be stronger
- [ ] Expand `MapProperties` with only verified safe toggles
- [ ] Expand `MapUiSettings` with only meaningful, stable controls
- [ ] Decide if `MapEffect` needs coroutine or keyed variants beyond current API
- [ ] Audit saveable behavior across process death and configuration changes

### Milestone 8: Docs

- [x] Add repository roadmap/checklist
- [x] Add benchmark notes document
- [x] Add README Compose quickstart seed content
- [ ] Expand README quickstart to a full happy-path guide
- [ ] Add marker docs with short snippets
- [ ] Add shapes docs with short snippets
- [ ] Add clustering docs with caveats
- [ ] Add XML-to-Compose migration guide
- [ ] Add contribution guide for upstream PR prep

### Milestone 9: Benchmark Track

- [x] Create benchmark comparison document
- [ ] Score current OLA Compose API against Google Maps Compose
- [ ] Score current OLA Compose API against Mapbox Compose
- [ ] Score current OLA Compose API against MapLibre Compose
- [ ] Score current OLA Compose API against Mappls / MapMyIndia Android DX
- [ ] Score current OLA Compose API against TomTom / HERE Compose direction
- [ ] Convert benchmark findings into concrete implementation backlog
- [ ] Use the benchmark to drive naming, docs, and sample cleanup

### Milestone 10: Showcase Readiness

- [ ] Polish sample visuals enough for screenshots and screen recordings
- [ ] Produce a minimal code snippet for README / LinkedIn / Twitter
- [ ] Prepare before/after XML vs Compose examples
- [ ] Make sure the library story is contribution-ready, not just sample-ready
- [ ] Run final smoke compile before any public share or upstream PR

## Next Working Order

### Immediate

- [ ] Expand `MapProperties`
- [ ] Expand `MapUiSettings`
- [ ] Audit public API naming drift
- [ ] Write README usage snippets for marker, shapes, clustering

### After Immediate

- [ ] Improve clustering ergonomics
- [ ] Split sample into parity demo plus polished quickstart demo
- [ ] Write XML-to-Compose migration doc
- [ ] Turn benchmark matrix into implementation backlog

### Final Polish

- [ ] Contribution notes
- [ ] Demo assets
- [ ] Showcase copy for social / upstream narrative

## Verification Standard

- [x] `:ola-maps-compose:compileDebugKotlin`
- [x] `:maps-sdk-sample:compileDebugKotlin`
- [ ] Smoke-test every sample section after each major API expansion
- [ ] Re-run compile verification after every roadmap milestone
