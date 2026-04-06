# Ola Maps Compose Roadmap

## Status

- [x] Create `ola-maps-compose` module
- [x] Add `OlaMap` composable host
- [x] Add lifecycle handling for `OlaMapView`
- [x] Add `CameraPositionState`
- [x] Add `MapProperties` and `MapUiSettings`
- [x] Add declarative content runtime
- [x] Add `Marker` and `MarkerState`
- [x] Add `MapEffect`
- [x] Add `Polyline`
- [x] Add `Polygon`
- [x] Add `Circle`
- [x] Add `BezierCurve`
- [x] Add `ClusteredMarkers`
- [x] Add marker click routing
- [x] Launch sample app into Compose activity by default
- [x] Wire API key from `local.properties`

## Current Gaps

- [ ] Tighten `MapProperties` to cover more safe runtime toggles
- [ ] Tighten `MapUiSettings` to match the useful XML controls surface
- [ ] Add saveable camera and marker state ergonomics where appropriate
- [ ] Verify whether info window click events are exposed by the OLA SDK
- [ ] Decide whether shape click callbacks are possible with the current AAR
- [ ] Expand clustering API beyond raw GeoJSON / `FeatureCollection`
- [ ] Add more polished sample scenarios instead of one catch-all screen
- [ ] Add XML to Compose migration documentation
- [ ] Add contribution notes for upstreaming to Ola Maps

## Quality Bar

- [ ] Match Google Maps Compose on API clarity for the supported OLA feature set
- [ ] Keep raw SDK escape hatches without forcing users into them
- [ ] Prefer Compose-first types over string-heavy SDK builders
- [ ] Ensure sample code is presentation-ready for external sharing
- [ ] Keep sample app as the primary smoke test surface during development

## Next Execution Order

- [ ] Refine state and settings API surface
- [ ] Write README Compose quickstart and examples
- [ ] Build benchmark matrix against Google, Mapbox, MapLibre, Mappls, TomTom/HERE
- [ ] Turn benchmark findings into implementation backlog
- [ ] Prepare showcase-quality demo and contribution narrative
