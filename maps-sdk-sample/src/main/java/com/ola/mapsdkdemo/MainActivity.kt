package com.ola.mapsdkdemo

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.ola.mapsdk.camera.MapControlSettings
import com.ola.mapsdk.interfaces.OlaMapCallback
import com.ola.mapsdk.listeners.OlaMapsCameraListenerManager
import com.ola.mapsdk.listeners.OlaMapsListenerManager
import com.ola.mapsdk.model.BezierCurveOptions
import com.ola.mapsdk.model.FeatureCollection
import com.ola.mapsdk.model.OlaCircleOptions
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaMarkerClusterOptions
import com.ola.mapsdk.model.OlaMarkerOptions
import com.ola.mapsdk.model.OlaPolygonOptions
import com.ola.mapsdk.model.OlaPolylineOptions
import com.ola.mapsdk.view.BezierCurve
import com.ola.mapsdk.view.Circle
import com.ola.mapsdk.view.ClusteredMarkers
import com.ola.mapsdk.view.Marker
import com.ola.mapsdk.view.OlaMap
import com.ola.mapsdk.view.OlaMapView
import com.ola.mapsdk.view.Polygon
import com.ola.mapsdk.view.Polyline
import org.maplibre.android.style.layers.Property

class MainActivity : AppCompatActivity()   {
    private lateinit var mapView: OlaMapView
    private lateinit var olaMap: OlaMap
    private var marker1: Marker? = null
    private var polyline: Polyline? = null
    private var bezierCurve: BezierCurve? = null
    private var polygon: Polygon? = null
    private var circle: Circle? = null
    private var clusteredMarkers: ClusteredMarkers? = null
    private lateinit var btnTest1: Button
    private lateinit var btnTest2: Button
    private lateinit var btnTest3: Button
    private lateinit var btnTest4: Button
    private lateinit var btnTest5: Button
    private lateinit var btnTest6: Button
    private lateinit var btnTest7: Button
    private lateinit var btnTest8: Button
    private lateinit var btnTest9: Button
    private lateinit var btnTest10: Button
    private lateinit var tvMapEvents: TextView
    private val location1 = OlaLatLng(12.931423492103944, 77.61648476788898)
    private val location2 = OlaLatLng(12.931758797710456, 77.61436504365439)
    // Additional locations for testing camera fit functionality
    private val location3 = OlaLatLng(19.305924882853862, 72.78733286147022)
    private val location4 = OlaLatLng(12.925000, 77.610000)
    private val requestLocationPermission = 1 // Define a request code


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnTest1 = findViewById(R.id.btnTest1)
        btnTest2 = findViewById(R.id.btnTest2)
        btnTest3 = findViewById(R.id.btnTest3)
        btnTest4 = findViewById(R.id.btnTest4)
        btnTest5 = findViewById(R.id.btnTest5)
        btnTest6 = findViewById(R.id.btnTest6)
        btnTest7 = findViewById(R.id.btnTest7)
        btnTest8 = findViewById(R.id.btnTest8)
        btnTest9 = findViewById(R.id.btnTest9)
        btnTest10 = findViewById(R.id.btnTest10)
        // tvMapEvents will be created programmatically after map loads

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnLoadMap).setOnClickListener {
            checkRunTimePermission()
            findViewById<Button>(R.id.btnLoadMap).visibility = View.GONE
        }
        Log.d("Test", "CICD Test4")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    enum class ActionName {
        UiControls,
        UiSettings,
        CurrentLocation,
        Marker,
        MarkerClustering,
        Infowindow,
        Circle,
        Polygon,
        BezierCurve,
        Polyline,
        MapEvents
    }

    private fun setAction(actionName: ActionName) {
        hideViews()

        when (actionName) {

            ActionName.MapEvents -> {
                tvMapEvents.text = "Click/Move/Long Click on the map to see the events"
                tvMapEvents.visibility = View.VISIBLE

                olaMap.setOnOlaMapsCameraIdleListener(object :
                    OlaMapsCameraListenerManager.OnOlaMapsCameraIdleListener {
                    override fun onOlaMapsCameraIdle() {
                        tvMapEvents.setText("onOlaMapsCameraIdle()")
                    }
                })

                olaMap.setOnMapLongClickedListener(object :
                    OlaMapsListenerManager.OnOlaMapsLongClickedListener {
                    override fun onOlaMapsLongClicked(olaLatLng: OlaLatLng) {
                        tvMapEvents.setText("onOlaMapsLongClicked()")
                    }
                })

                olaMap.setOnMapClickedListener(object :
                    OlaMapsListenerManager.OnOlaMapClickedListener {
                    override fun onOlaMapClicked(olaLatLng: OlaLatLng) {
                        tvMapEvents.setText("onOlaMapClicked()")
                    }
                })

                olaMap.setOnOlaMapsCameraMoveListener(object :
                    OlaMapsCameraListenerManager.OnOlaMapsCameraMoveListener {
                    override fun onOlaMapsCameraMove() {
                        tvMapEvents.setText("onOlaMapsCameraMove()")
                    }
                })

            }

            ActionName.Polyline -> {
                actionBar.apply {
                    title = "Polyline"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {
                    olaMap.moveCameraToLatLong(location1, 15.0, durationMs = 1000)
                    polyline?.removePolyline()
                    polyline = olaMap.addPolyline(OlaPolylineOptions.Builder().setPolylineId("a").setPoints(arrayListOf(location1, location2)).build())
                }

                btnTest2.setOnClickListener {
                    polyline?.removePolyline()
                }

                btnTest3.setOnClickListener {
                    polyline?.setColor("#FF0000")
                }
            }

            ActionName.UiControls -> {
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                actionBar.apply {
                    title = "Ui Controls"
                }

                btnTest1.setText("Zoom in")
                btnTest2.setText("Zoom out")

                btnTest1.setOnClickListener {
                    this.olaMap.zoomToLocation(this.olaMap.getCurrentOlaCameraPosition()!!.target!!,this.olaMap.getCurrentOlaCameraPosition()!!.zoomLevel+1)
                }

                btnTest2.setOnClickListener {
                    this.olaMap.zoomToLocation(this.olaMap.getCurrentOlaCameraPosition()!!.target!!,this.olaMap.getCurrentOlaCameraPosition()!!.zoomLevel-1)

                }
            }

            ActionName.CurrentLocation -> {
                actionBar.apply {
                    title = "Current location"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE

                btnTest1.setText("Show")
                btnTest2.setText("Hide")


                btnTest1.setOnClickListener {
                    this.olaMap.showCurrentLocation()
                    this.olaMap.getCurrentLocation()?.let {
                        this.olaMap.moveCameraToLatLong(it, 15.0, durationMs = 100)

                    }
                }


                btnTest2.setOnClickListener {
                    this.olaMap.hideCurrentLocation()
                }

            }

            ActionName.Infowindow -> {
                actionBar.apply {
                    title = "Infowindow"
                }

                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Hide")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {

                    olaMap.moveCameraToLatLong(location1, 15.0, durationMs = 1000)

                    val markerOptions1 =
                        OlaMarkerOptions.Builder().setMarkerId("1232").setPosition(location1)
                            .setSnippet("This is the infowindow")
                            .setIsIconClickable(true)
                            .setIconRotation(0f)
                            .setIsAnimationEnable(true)
                            .setIsInfoWindowDismissOnClick(true)
                            .build()
                    marker1 = olaMap.addMarker(markerOptions1)
                }

                btnTest2.setOnClickListener {
                    marker1?.hideInfoWindow()
                }

                btnTest3.setOnClickListener {
                    marker1?.updateInfoWindow("This is updated info window")
                }
            }

            ActionName.Circle -> {
                actionBar.apply {
                    title = "Circle"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {
                    olaMap.moveCameraToLatLong(location1, 15.0, durationMs = 1000)
                    circle?.removeCircle()
                    circle = olaMap.addCircle(OlaCircleOptions.Builder().setOlaLatLng(location1).setRadius(100f).build())
                }

                btnTest2.setOnClickListener {
                    circle?.removeCircle()
                }

                btnTest3.setOnClickListener {
                    circle?.setColor("#FF0000")
                }
            }

            ActionName.Polygon -> {
                actionBar.apply {
                    title = "Polygon"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {
                    olaMap.moveCameraToLatLong(OlaLatLng(12.964623365273798, 77.59560740718187), 15.0, durationMs = 1000)
                    polygon = olaMap.addPolygon(OlaPolygonOptions.Builder().setPolygonId("polygon1").setPoints(arrayListOf(OlaLatLng(12.970467915225672, 77.58815217917578), OlaLatLng(12.962675151452494, 77.57848953729078), OlaLatLng(12.964623365273798, 77.59560740718187))).build())
                }

                btnTest2.setOnClickListener {
                    polygon?.removePolygon()
                }

                btnTest3.setOnClickListener {
                    polygon?.setColor("#FF0000")
                }
            }

            ActionName.BezierCurve -> {
                actionBar.apply {
                    title = "Bezier Curve"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {
                    olaMap.moveCameraToLatLong(location1, 15.0, durationMs = 1000)
                    bezierCurve = olaMap.addBezierCurve(BezierCurveOptions.Builder().setCurveId("curve1").setStartPoint(location1).setEndPoint(location2).build())
                }

                btnTest2.setOnClickListener {
                    bezierCurve?.removeBezierCurve()
                }

                btnTest3.setOnClickListener {
                    bezierCurve?.setColor("#FF0000")
                }
            }


            ActionName.Marker -> {
                actionBar.apply {
                    title = "Marker"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE
                btnTest4.visibility = View.VISIBLE
                btnTest5.visibility = View.VISIBLE
                btnTest6.visibility = View.VISIBLE
                btnTest7.visibility = View.VISIBLE
                btnTest8.visibility = View.VISIBLE
                btnTest9.visibility = View.VISIBLE
                btnTest10.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Set Position")
                btnTest4.setText("Update Size")
                btnTest5.setText("Update Rotation")
                btnTest6.setText("Update Info")
                btnTest7.setText("Update Offset")
                btnTest8.setText("Update Anchor")
                btnTest9.setText("Change Icon")
                btnTest10.setText("Fit View")

                btnTest1.setOnClickListener {

                    olaMap.moveCameraToLatLong(location1, 15.0, durationMs = 1000)

                    val markerOptions1 =
                        OlaMarkerOptions.Builder().setMarkerId("marker1").setPosition(location1)
                            .setIsIconClickable(true)
                            .setIconIntRes(R.drawable.ic_top_truck)
                            .setIconRotation(0f)
                            .setIconSize(1.0f)
                            .setIsAnimationEnable(true)
                            .setIsInfoWindowDismissOnClick(true)
                            .build()
                    marker1 = olaMap.addMarker(markerOptions1)
                }

                btnTest2.setOnClickListener {
                    marker1?.removeMarker()
                }

                btnTest3.setOnClickListener {
                    marker1?.setPosition(location2)
                }

                btnTest4.setOnClickListener {
                    // Update marker size using updateMarker method
                    marker1?.updateIconSize(iconSize = 1.5f)
                    Toast.makeText(this@MainActivity, "Marker size updated to 1.5x", Toast.LENGTH_SHORT).show()
                }

                btnTest5.setOnClickListener {
                    // Update marker rotation using updateMarker method
                    marker1?.updateIconRotation(iconRotation = 45f)
                    Toast.makeText(this@MainActivity, "Marker rotated to 45 degrees", Toast.LENGTH_SHORT).show()
                }

                btnTest6.setOnClickListener {
                    // Update marker info window and snippet using updateMarker method
                    marker1?.updateSnippet(
                        snippet = "Updated marker info - Size: 1.5x, Rotation: 45°",
                    )
                    marker1?.updateSubSnippet(
                        subSnippet = "Updated marker info - Size: 1.5x, Rotation: 45°",
                    )

                    Toast.makeText(this@MainActivity, "Marker info updated", Toast.LENGTH_SHORT).show()
                }

                btnTest7.setOnClickListener {
                    // Update marker icon offset using updateMarker method
                    marker1?.updateIconOffset(iconOffset = arrayOf(10f, -5f))
                    Toast.makeText(this@MainActivity, "Marker offset updated", Toast.LENGTH_SHORT).show()
                }

                btnTest8.setOnClickListener {
                    // Update marker anchor using updateMarker method
                    marker1?.updateIconAnchor(iconAnchor = Property.ICON_ANCHOR_BOTTOM)
                    Toast.makeText(this@MainActivity, "Marker anchor updated to bottom", Toast.LENGTH_SHORT).show()
                }

                btnTest9.setOnClickListener {
                    // Update marker icon using updateMarker method - change from truck to car
                    marker1?.updateIconIntRes(iconIntRes = R.drawable.ic_car_top)
                    Toast.makeText(this@MainActivity, "Marker icon changed to car", Toast.LENGTH_SHORT).show()
                }

                btnTest10.setOnClickListener {
                    // Test the new easeCamera method with multiple points
                    val points: MutableList<OlaLatLng> = arrayListOf(location1, location2, location3, location4)
                    
                    // First, add markers at all these locations for visual reference
                    val markerOptions2 = OlaMarkerOptions.Builder()
                        .setMarkerId("marker2")
                        .setPosition(location2)
                        .setIconIntRes(R.drawable.ic_dot)
                        .setIconSize(0.8f)
                        .build()
                    olaMap.addMarker(markerOptions2)
                    
                    val markerOptions3 = OlaMarkerOptions.Builder()
                        .setMarkerId("marker3")
                        .setPosition(location3)
                        .setIconIntRes(R.drawable.ic_dot)
                        .setIconSize(0.8f)
                        .build()
                    olaMap.addMarker(markerOptions3)
                    
                    val markerOptions4 = OlaMarkerOptions.Builder()
                        .setMarkerId("marker4")
                        .setPosition(location4)
                        .setIconIntRes(R.drawable.ic_dot)
                        .setIconSize(0.8f)
                        .build()
                    olaMap.addMarker(markerOptions4)
                    
                    // Now use the new easeCamera method to fit all points in view
                    olaMap.easeCamera(points, 2000)
                    Toast.makeText(this@MainActivity, "Camera fitted to show all points", Toast.LENGTH_SHORT).show()
                }
            }

            ActionName.MarkerClustering -> {
                actionBar.apply {
                    title = "Marker Clustering"
                }
                btnTest1.visibility = View.VISIBLE
                btnTest2.visibility = View.VISIBLE
                btnTest3.visibility = View.VISIBLE

                btnTest1.setText("Add")
                btnTest2.setText("Remove")
                btnTest3.setText("Update")

                btnTest1.setOnClickListener {
                    // First move camera to a good viewing position for earthquake data in North America
                    olaMap.moveCameraToLatLong(OlaLatLng(45.0, -120.0), 4.5, durationMs = 1000)
                    
                    // Add a delay to ensure camera movement completes before adding markers
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({

                    val json = "{\"type\": \"FeatureCollection\", \"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\" } }, \"features\": [{ \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821840\", \"mag\": 1.4, \"time\": 1504843458180, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -150.7807, 61.7731, 61.9 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821839\", \"mag\": 1.1, \"time\": 1504843388032, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -149.5616, 61.4081, 45.5 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777570\", \"mag\": 1.9, \"time\": 1504843130740, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -173.812, 51.8664, 11.6 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821837\", \"mag\": 1.7, \"time\": 1504843122073, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -154.717, 58.7435, 117.7 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821836\", \"mag\": 1.3, \"time\": 1504842507708, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -150.7323, 60.5323, 52.2 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757551\", \"mag\": 1.47, \"time\": 1504841647940, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -116.7945, 33.496333, 3.33 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahus\", \"mag\": 4.7, \"time\": 1504841042960, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -169.0592, 52.1619, 10.0 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777419\", \"mag\": 1.0, \"time\": 1504839731548, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -153.81, 64.7157, 15.2 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777418\", \"mag\": 2.4, \"time\": 1504839437977, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -174.7414, 52.1837, 13.2 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821832\", \"mag\": 2.4, \"time\": 1504839217735, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -169.2407, 52.3318, 38.5 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900746\", \"mag\": 2.1, \"time\": 1504839173590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -154.978833, 19.772, 43.163 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ismpkansas70234763\", \"mag\": 1.85, \"time\": 1504838718270, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -97.887167, 37.203, 6.15 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757519\", \"mag\": 1.49, \"time\": 1504838267430, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -118.945167, 34.213667, 19.49 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahub\", \"mag\": 4.3, \"time\": 1504837583700, \"felt\": 823, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -97.683, 36.6996, 6.073 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777416\", \"mag\": 1.3, \"time\": 1504836895690, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -150.6982, 63.5777, 11.7 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"uw61304877\", \"mag\": 1.15, \"time\": 1504836765080, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -122.568333, 48.699167, 3.29 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahu8\", \"mag\": 3.7, \"time\": 1504836433340, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -111.4569, 42.6238, 5.0 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888096\", \"mag\": 2.03, \"time\": 1504835142230, \"felt\": 4, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -122.053667, 37.835167, 7.59 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821829\", \"mag\": 1.5, \"time\": 1504834613166, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -163.7652, 67.5597, 6.1 ] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900626\", \"mag\": 2.91, \"time\": 1504833891990, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [ -155.011833, 19.399333, 2.609 ] } } ] }"
                    val featureCollection = Gson().fromJson(json, FeatureCollection::class.java)

                    clusteredMarkers =  olaMap.addClusteredMarkers(
                        olaMarkerClusterOptions = OlaMarkerClusterOptions.Builder()
                            //.setDefaultMarkerIcon(BitmapFactory.decodeResource(resources, com.ola.mapsdk.R.drawable.orch_current_location))
                            .setClusterRadius(35)
                            .setDefaultMarkerColor("#00FFFF")
                            .setDefaultClusterColor("#FF0000")
                            .setStop1Color("#00FF00")
                            .setStop2Color("#0000FF")
                            .build(),
                        featureCollection = featureCollection

                        //"{ \"type\": \"FeatureCollection\", \"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\" } }, \"features\": [ { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16994521\", \"mag\": 2.3, \"time\": 1507425650893, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-151.5129, 63.1016, 0] } },{ \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr6\", \"mag\": 4.2, \"time\": 1504852477520, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.3508, 15.2654, 45.29] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888331\", \"mag\": 1.19, \"time\": 1504852408190, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.736, 38.759167, 1.73] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr7\", \"mag\": 4, \"time\": 1504852398530, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.7954, 14.8479, 70.92] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604169\", \"mag\": 1.2, \"time\": 1504852379590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.2104, 36.4921, 0] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahxc\", \"mag\": 4.7, \"time\": 1504852332790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [3.8455, 72.6309, 10] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888321\", \"mag\": 1.8, \"time\": 1504852052730, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.8065, 38.792167, 2.94] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahx3\", \"mag\": 4.3, \"time\": 1504851979140, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.627, 15.4476, 46.76] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777937\", \"mag\": 2.1, \"time\": 1504851258875, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-152.4392, 60.165, 78.3] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr1\", \"mag\": 4.3, \"time\": 1504851196580, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9103, 15.3461, 69.88] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821848\", \"mag\": 1.5, \"time\": 1504851158790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-152.7947, 60.1163, 106] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahwt\", \"mag\": 4.6, \"time\": 1504850928420, \"felt\": 1, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5535, 15.437, 48.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr4\", \"mag\": 4.3, \"time\": 1504850711370, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.9041, 15.1749, 40.34] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777931\", \"mag\": 1.3, \"time\": 1504850655355, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-156.0257, 67.0569, 3.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr2\", \"mag\": 4.2, \"time\": 1504850627530, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-95.1989, 15.0884, 40.99] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arrk\", \"mag\": 4.2, \"time\": 1504850557880, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.7531, 15.4362, 36.4] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888301\", \"mag\": 1.87, \"time\": 1504850251760, \"felt\": 4, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.802333, 38.820667, 3.26] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahwk\", \"mag\": 4.6, \"time\": 1504850223990, \"felt\": 0, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.1219, 15.2941, 54.75] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahx1\", \"mag\": 4.3, \"time\": 1504850059440, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9647, 15.304, 72.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3u\", \"mag\": 4.2, \"time\": 1504849825570, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.712, 15.4284, 47.14] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888296\", \"mag\": 1.15, \"time\": 1504849761840, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800667, 38.847667, 0.54] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3t\", \"mag\": 4.4, \"time\": 1504849737970, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.8795, 15.668, 47.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888271\", \"mag\": 1.56, \"time\": 1504849628820, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.8005, 38.846333, 0.64] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888266\", \"mag\": 1.02, \"time\": 1504849513230, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.801666, 38.847332, 0.61] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahw5\", \"mag\": 5, \"time\": 1504849467850, \"felt\": 1, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.7228, 15.5333, 34.69] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr3\", \"mag\": 4.3, \"time\": 1504849426720, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.0216, 15.4454, 53.87] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888261\", \"mag\": 0.99, \"time\": 1504849348500, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.761002, 38.832501, 0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604165\", \"mag\": 2.1, \"time\": 1504849234193, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-114.9828, 37.2854, 2.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr0\", \"mag\": 4.9, \"time\": 1504849156050, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9438, 15.1471, 68.27] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3s\", \"mag\": 4.7, \"time\": 1504849019590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.8391, 15.6629, 60.73] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arux\", \"mag\": 5.1, \"time\": 1504848867960, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.0987, 14.9089, 42.35] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ai3y\", \"mag\": 2.5, \"time\": 1504848837100, \"felt\": 3, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-98.4714, 36.5024, 6.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvy\", \"mag\": 5.2, \"time\": 1504848817820, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.4082, 15.2843, 45.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqy\", \"mag\": 4.6, \"time\": 1504848547140, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9331, 15.1504, 39.51] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888241\", \"mag\": 1.03, \"time\": 1504848340890, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800835, 38.847832, 0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvs\", \"mag\": 5.2, \"time\": 1504848276840, \"felt\": 3, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5946, 15.2029, 49.18] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888226\", \"mag\": 1.48, \"time\": 1504848105550, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.802, 38.848167, 0.48] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604164\", \"mag\": 1.9, \"time\": 1504848096515, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-115.8669, 37.2911, 8.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3q\", \"mag\": 4.8, \"time\": 1504848059390, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.6096, 15.9617, 41.51] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvc\", \"mag\": 5.3, \"time\": 1504847863090, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5672, 15.5726, 51.13] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107624\", \"mag\": 2.16, \"time\": 1504847827370, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.752167, 39.287167, 10.78] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888211\", \"mag\": 1.01, \"time\": 1504847761210, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.765663, 38.848667, 6.01] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888201\", \"mag\": 1.17, \"time\": 1504847709790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.80883, 38.829666, 0.99] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"pr2017251000\", \"mag\": 3.73, \"time\": 1504847643290, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-68.2905, 19.3283, 52] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqw\", \"mag\": 4.8, \"time\": 1504847625230, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.8789, 15.4565, 61.03] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqv\", \"mag\": 5, \"time\": 1504847479550, \"felt\": null, \"tsunami\": 1 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [128.5159, 2.4289, 235.24] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107629\", \"mag\": 1.35, \"time\": 1504847454920, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.805833, 38.815333, 2.23] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888186\", \"mag\": 1.45, \"time\": 1504847378350, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.766, 38.8255, 0.39] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888181\", \"mag\": 2.28, \"time\": 1504847361610, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.7555, 38.775667, 0.02] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888171\", \"mag\": 1.83, \"time\": 1504847323920, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.7715, 38.817333, -0.11] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107399\", \"mag\": 1.12, \"time\": 1504847319000, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.771167, 38.819333, -0.31] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888156\", \"mag\": 1.24, \"time\": 1504847297950, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.556, 38.807, 15.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888151\", \"mag\": 1.36, \"time\": 1504847289840, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.806, 38.822, 1.41] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888176\", \"mag\": 1.79, \"time\": 1504847279970, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.739667, 38.774, -0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888146\", \"mag\": 2.3, \"time\": 1504847258860, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.817833, 38.815167, 0.34] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888141\", \"mag\": 2.42, \"time\": 1504847223340, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800667, 38.83, 1.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahv7\", \"mag\": 5.7, \"time\": 1504846893100, \"felt\": 26, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.2707, 15.1746, 35.87] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777708\", \"mag\": 1.1, \"time\": 1504846594461, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-151.0631, 61.4229, 6.8] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"mb80252994\", \"mag\": 1.72, \"time\": 1504846249390, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-112.541833, 46.857167, 12.55] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahv0\", \"mag\": 8.1, \"time\": 1504846160000, \"felt\": 2494, \"tsunami\": 1 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9067, 15.0356, 56.67] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ai6a\", \"mag\": 2.5, \"time\": 1504846040410, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-163.4753, 53.7845, 22.98] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888131\", \"mag\": 1.2, \"time\": 1504845594500, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-118.8105, 37.463833, -1.37] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821844\", \"mag\": 2, \"time\": 1504845256450, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [178.621, 51.2706, 13.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757591\", \"mag\": 1.24, \"time\": 1504844674340, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.899167, 34.321833, 9.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821842\", \"mag\": 2, \"time\": 1504843966513, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [179.0405, 51.3724, 44.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821841\", \"mag\": 2.4, \"time\": 1504843627204, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-165.0538, 52.2197, 10.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821840\", \"mag\": 1.4, \"time\": 1504843458180, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.7807, 61.7731, 61.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821839\", \"mag\": 1.1, \"time\": 1504843388032, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-149.5616, 61.4081, 45.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777570\", \"mag\": 1.9, \"time\": 1504843130740, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-173.812, 51.8664, 11.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821837\", \"mag\": 1.7, \"time\": 1504843122073, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-154.717, 58.7435, 117.7] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821836\", \"mag\": 1.3, \"time\": 1504842507708, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.7323, 60.5323, 52.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757551\", \"mag\": 1.47, \"time\": 1504841647940, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.7945, 33.496333, 3.33] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahus\", \"mag\": 4.7, \"time\": 1504841042960, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-169.0592, 52.1619, 10] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777419\", \"mag\": 1, \"time\": 1504839731548, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-153.81, 64.7157, 15.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777418\", \"mag\": 2.4, \"time\": 1504839437977, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-174.7414, 52.1837, 13.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821832\", \"mag\": 2.4, \"time\": 1504839217735, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-169.2407, 52.3318, 38.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900746\", \"mag\": 2.1, \"time\": 1504839173590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-154.978833, 19.772, 43.163] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ismpkansas70234763\", \"mag\": 1.85, \"time\": 1504838718270, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-97.887167, 37.203, 6.15] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757519\", \"mag\": 1.49, \"time\": 1504838267430, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-118.945167, 34.213667, 19.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahub\", \"mag\": 4.3, \"time\": 1504837583700, \"felt\": 823, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-97.683, 36.6996, 6.073] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777416\", \"mag\": 1.3, \"time\": 1504836895690, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.6982, 63.5777, 11.7] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"uw61304877\", \"mag\": 1.15, \"time\": 1504836765080, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.568333, 48.699167, 3.29] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahu8\", \"mag\": 3.7, \"time\": 1504836433340, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-111.4569, 42.6238, 5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888096\", \"mag\": 2.03, \"time\": 1504835142230, \"felt\": 4, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.053667, 37.835167, 7.59] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821829\", \"mag\": 1.5, \"time\": 1504834613166, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-163.7652, 67.5597, 6.1] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900626\", \"mag\": 2.91, \"time\": 1504833891990, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-155.011833, 19.399333, 2.609] } } ] }"
                    )
                    }, 1200) // Wait for camera animation to complete
                }

                btnTest2.setOnClickListener {
                    clusteredMarkers?.removeClusteredMarkers()
                }

                btnTest3.setOnClickListener {
                    val newOptions = OlaMarkerClusterOptions.Builder()
                        //.setDefaultMarkerIcon(BitmapFactory.decodeResource(resources, com.ola.mapsdk.R.drawable.orch_current_location))
                        .setClusterRadius(35)
                        .setDefaultMarkerColor("#00FFFF")
                        .setDefaultClusterColor("#0000FF")
                        .setStop1Color("#00FF00")
                        .setStop2Color("#FF0000")
                        .build()

                    val newData = "{ \"type\": \"FeatureCollection\", \"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\" } }, \"features\": [ { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16994521\", \"mag\": 2.3, \"time\": 1507425650893, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-151.5129, 63.1016, 0] } },{ \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr6\", \"mag\": 4.2, \"time\": 1504852477520, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.3508, 15.2654, 45.29] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888331\", \"mag\": 1.19, \"time\": 1504852408190, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.736, 38.759167, 1.73] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr7\", \"mag\": 4, \"time\": 1504852398530, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.7954, 14.8479, 70.92] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604169\", \"mag\": 1.2, \"time\": 1504852379590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.2104, 36.4921, 0] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahxc\", \"mag\": 4.7, \"time\": 1504852332790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [3.8455, 72.6309, 10] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888321\", \"mag\": 1.8, \"time\": 1504852052730, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.8065, 38.792167, 2.94] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahx3\", \"mag\": 4.3, \"time\": 1504851979140, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.627, 15.4476, 46.76] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777937\", \"mag\": 2.1, \"time\": 1504851258875, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-152.4392, 60.165, 78.3] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr1\", \"mag\": 4.3, \"time\": 1504851196580, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9103, 15.3461, 69.88] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821848\", \"mag\": 1.5, \"time\": 1504851158790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-152.7947, 60.1163, 106] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahwt\", \"mag\": 4.6, \"time\": 1504850928420, \"felt\": 1, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5535, 15.437, 48.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr4\", \"mag\": 4.3, \"time\": 1504850711370, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.9041, 15.1749, 40.34] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777931\", \"mag\": 1.3, \"time\": 1504850655355, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-156.0257, 67.0569, 3.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr2\", \"mag\": 4.2, \"time\": 1504850627530, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-95.1989, 15.0884, 40.99] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arrk\", \"mag\": 4.2, \"time\": 1504850557880, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.7531, 15.4362, 36.4] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888301\", \"mag\": 1.87, \"time\": 1504850251760, \"felt\": 4, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.802333, 38.820667, 3.26] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahwk\", \"mag\": 4.6, \"time\": 1504850223990, \"felt\": 0, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.1219, 15.2941, 54.75] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahx1\", \"mag\": 4.3, \"time\": 1504850059440, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9647, 15.304, 72.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3u\", \"mag\": 4.2, \"time\": 1504849825570, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.712, 15.4284, 47.14] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888296\", \"mag\": 1.15, \"time\": 1504849761840, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800667, 38.847667, 0.54] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3t\", \"mag\": 4.4, \"time\": 1504849737970, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.8795, 15.668, 47.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888271\", \"mag\": 1.56, \"time\": 1504849628820, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.8005, 38.846333, 0.64] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888266\", \"mag\": 1.02, \"time\": 1504849513230, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.801666, 38.847332, 0.61] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahw5\", \"mag\": 5, \"time\": 1504849467850, \"felt\": 1, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.7228, 15.5333, 34.69] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr3\", \"mag\": 4.3, \"time\": 1504849426720, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.0216, 15.4454, 53.87] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888261\", \"mag\": 0.99, \"time\": 1504849348500, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.761002, 38.832501, 0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604165\", \"mag\": 2.1, \"time\": 1504849234193, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-114.9828, 37.2854, 2.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arr0\", \"mag\": 4.9, \"time\": 1504849156050, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9438, 15.1471, 68.27] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3s\", \"mag\": 4.7, \"time\": 1504849019590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.8391, 15.6629, 60.73] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arux\", \"mag\": 5.1, \"time\": 1504848867960, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.0987, 14.9089, 42.35] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ai3y\", \"mag\": 2.5, \"time\": 1504848837100, \"felt\": 3, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-98.4714, 36.5024, 6.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvy\", \"mag\": 5.2, \"time\": 1504848817820, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.4082, 15.2843, 45.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqy\", \"mag\": 4.6, \"time\": 1504848547140, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9331, 15.1504, 39.51] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888241\", \"mag\": 1.03, \"time\": 1504848340890, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800835, 38.847832, 0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvs\", \"mag\": 5.2, \"time\": 1504848276840, \"felt\": 3, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5946, 15.2029, 49.18] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888226\", \"mag\": 1.48, \"time\": 1504848105550, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.802, 38.848167, 0.48] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nn00604164\", \"mag\": 1.9, \"time\": 1504848096515, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-115.8669, 37.2911, 8.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ay3q\", \"mag\": 4.8, \"time\": 1504848059390, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.6096, 15.9617, 41.51] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahvc\", \"mag\": 5.3, \"time\": 1504847863090, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.5672, 15.5726, 51.13] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107624\", \"mag\": 2.16, \"time\": 1504847827370, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.752167, 39.287167, 10.78] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888211\", \"mag\": 1.01, \"time\": 1504847761210, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.765663, 38.848667, 6.01] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888201\", \"mag\": 1.17, \"time\": 1504847709790, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.80883, 38.829666, 0.99] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"pr2017251000\", \"mag\": 3.73, \"time\": 1504847643290, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-68.2905, 19.3283, 52] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqw\", \"mag\": 4.8, \"time\": 1504847625230, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.8789, 15.4565, 61.03] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000arqv\", \"mag\": 5, \"time\": 1504847479550, \"felt\": null, \"tsunami\": 1 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [128.5159, 2.4289, 235.24] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107629\", \"mag\": 1.35, \"time\": 1504847454920, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.805833, 38.815333, 2.23] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888186\", \"mag\": 1.45, \"time\": 1504847378350, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.766, 38.8255, 0.39] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888181\", \"mag\": 2.28, \"time\": 1504847361610, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.7555, 38.775667, 0.02] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888171\", \"mag\": 1.83, \"time\": 1504847323920, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.7715, 38.817333, -0.11] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc71107399\", \"mag\": 1.12, \"time\": 1504847319000, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.771167, 38.819333, -0.31] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888156\", \"mag\": 1.24, \"time\": 1504847297950, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.556, 38.807, 15.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888151\", \"mag\": 1.36, \"time\": 1504847289840, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.806, 38.822, 1.41] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888176\", \"mag\": 1.79, \"time\": 1504847279970, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.739667, 38.774, -0.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888146\", \"mag\": 2.3, \"time\": 1504847258860, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.817833, 38.815167, 0.34] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888141\", \"mag\": 2.42, \"time\": 1504847223340, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.800667, 38.83, 1.81] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahv7\", \"mag\": 5.7, \"time\": 1504846893100, \"felt\": 26, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-94.2707, 15.1746, 35.87] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777708\", \"mag\": 1.1, \"time\": 1504846594461, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-151.0631, 61.4229, 6.8] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"mb80252994\", \"mag\": 1.72, \"time\": 1504846249390, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-112.541833, 46.857167, 12.55] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahv0\", \"mag\": 8.1, \"time\": 1504846160000, \"felt\": 2494, \"tsunami\": 1 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-93.9067, 15.0356, 56.67] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ai6a\", \"mag\": 2.5, \"time\": 1504846040410, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-163.4753, 53.7845, 22.98] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888131\", \"mag\": 1.2, \"time\": 1504845594500, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-118.8105, 37.463833, -1.37] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821844\", \"mag\": 2, \"time\": 1504845256450, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [178.621, 51.2706, 13.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757591\", \"mag\": 1.24, \"time\": 1504844674340, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.899167, 34.321833, 9.91] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821842\", \"mag\": 2, \"time\": 1504843966513, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [179.0405, 51.3724, 44.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821841\", \"mag\": 2.4, \"time\": 1504843627204, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-165.0538, 52.2197, 10.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821840\", \"mag\": 1.4, \"time\": 1504843458180, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.7807, 61.7731, 61.9] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821839\", \"mag\": 1.1, \"time\": 1504843388032, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-149.5616, 61.4081, 45.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777570\", \"mag\": 1.9, \"time\": 1504843130740, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-173.812, 51.8664, 11.6] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821837\", \"mag\": 1.7, \"time\": 1504843122073, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-154.717, 58.7435, 117.7] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821836\", \"mag\": 1.3, \"time\": 1504842507708, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.7323, 60.5323, 52.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757551\", \"mag\": 1.47, \"time\": 1504841647940, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-116.7945, 33.496333, 3.33] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahus\", \"mag\": 4.7, \"time\": 1504841042960, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-169.0592, 52.1619, 10] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777419\", \"mag\": 1, \"time\": 1504839731548, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-153.81, 64.7157, 15.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777418\", \"mag\": 2.4, \"time\": 1504839437977, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-174.7414, 52.1837, 13.2] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821832\", \"mag\": 2.4, \"time\": 1504839217735, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-169.2407, 52.3318, 38.5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900746\", \"mag\": 2.1, \"time\": 1504839173590, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-154.978833, 19.772, 43.163] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ismpkansas70234763\", \"mag\": 1.85, \"time\": 1504838718270, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-97.887167, 37.203, 6.15] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ci37757519\", \"mag\": 1.49, \"time\": 1504838267430, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-118.945167, 34.213667, 19.49] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahub\", \"mag\": 4.3, \"time\": 1504837583700, \"felt\": 823, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-97.683, 36.6996, 6.073] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16777416\", \"mag\": 1.3, \"time\": 1504836895690, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-150.6982, 63.5777, 11.7] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"uw61304877\", \"mag\": 1.15, \"time\": 1504836765080, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.568333, 48.699167, 3.29] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"us2000ahu8\", \"mag\": 3.7, \"time\": 1504836433340, \"felt\": 2, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-111.4569, 42.6238, 5] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"nc72888096\", \"mag\": 2.03, \"time\": 1504835142230, \"felt\": 4, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-122.053667, 37.835167, 7.59] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"ak16821829\", \"mag\": 1.5, \"time\": 1504834613166, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-163.7652, 67.5597, 6.1] } }, { \"type\": \"Feature\", \"properties\": { \"id\": \"hv61900626\", \"mag\": 2.91, \"time\": 1504833891990, \"felt\": null, \"tsunami\": 0 }, \"geometry\": { \"type\": \"Point\", \"coordinates\": [-155.011833, 19.399333, 2.609] } } ] }"

                    clusteredMarkers?.updateClusteredMarkers(geoJson = newData, olaMarkerClusterOptions = newOptions)
                }
            }

            ActionName.UiSettings -> {
                actionBar.apply {
                    title = "Ui Settings"
                }


                btnTest1.visibility = View.VISIBLE
                btnTest1.setText("Show Ui & Control settings")
                btnTest1.setOnClickListener {
                    val dialog = Dialog(this@MainActivity)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.ui_settings_dialog)


                    val cbRotateGestures = dialog.findViewById<View>(R.id.cbRotateGestures) as CheckBox
                    val cbScrollGestures = dialog.findViewById<View>(R.id.cbScrollGestures) as CheckBox
                    val cbZoomGestures = dialog.findViewById<View>(R.id.cbZoomGestures) as CheckBox
                    val cbCompass = dialog.findViewById<View>(R.id.cbCompass) as CheckBox
                    val cbTiltGestures = dialog.findViewById<View>(R.id.cbTiltGestures) as CheckBox
                    val cbDoubleTapGestures = dialog.findViewById<View>(R.id.cbDoubleTapGestures) as CheckBox


                    cbRotateGestures.isChecked = olaMap.getMapUiSettings().isRotateGesturesEnabled()
                    cbScrollGestures.isChecked = olaMap.getMapUiSettings().isScrollGesturesEnabled()
                    cbZoomGestures.isChecked = olaMap.getMapUiSettings().isZoomGesturesEnabled()
                    cbCompass.isChecked = olaMap.getMapUiSettings().isCompassEnabled()
                    cbTiltGestures.isChecked = olaMap.getMapUiSettings().isTiltGesturesEnabled()
                    cbDoubleTapGestures.isChecked = olaMap.getMapUiSettings().isDoubleTapGesturesEnabled()


                    val dialogButton = dialog.findViewById<View>(R.id.btnDoneUiSettings) as Button
                    dialogButton.setOnClickListener {
                        dialog.dismiss()

                        olaMap.updateMapUiSettings(
                            MapControlSettings.Builder()
                                .setRotateGesturesEnabled(cbRotateGestures.isChecked)
                                .setScrollGesturesEnabled(cbScrollGestures.isChecked)
                                .setZoomGesturesEnabled(cbZoomGestures.isChecked)
                                .setCompassEnabled(cbCompass.isChecked)
                                .setTiltGesturesEnabled(cbTiltGestures.isChecked)
                                .setDoubleTapGesturesEnabled(cbDoubleTapGestures.isChecked)
                                .build()
                        )
                    }
                    dialog.show()
                }


               }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(olaMap == null) {
           Toast.makeText(this, "Map is not ready yet, Please Load the map first", Toast.LENGTH_SHORT).show()
           return true
       }

        return when (item.itemId) {
            R.id.action_ui_controls -> {
                setAction(ActionName.UiControls)
                true
            }
            R.id.action_ui_settings -> {
                setAction(ActionName.UiSettings)
                true
            }
            R.id.action_current_location -> {
                setAction(ActionName.CurrentLocation)
                true
            }
            R.id.action_marker -> {
                setAction(ActionName.Marker)
                true
            }
            R.id.action_marker_clustering -> {
                setAction(ActionName.MarkerClustering)
                true
            }
            R.id.action_infowindow -> {
                setAction(ActionName.Infowindow)
                true
            }
            R.id.action_circle -> {
                setAction(ActionName.Circle)
                true
            }
            R.id.action_polygon -> {
                setAction(ActionName.Polygon)
                true
            }
            R.id.action_bezierCurve -> {
                setAction(ActionName.BezierCurve)
                true
            }
            R.id.action_polyline -> {
                setAction(ActionName.Polyline)
                true
            }
            R.id.action_events -> {
                setAction(ActionName.MapEvents)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initMap(){
        mapView = findViewById(R.id.mapView)
        mapView.getMap(
            apiKey = BuildConfig.OLA_MAPS_API_KEY,
            olaMapCallback = object : OlaMapCallback {
                override fun onMapReady(olaMap: OlaMap) {
                    this@MainActivity.olaMap = olaMap
                    createMapEventsTextView()
                }

                override fun onMapError(error: String) {}
            }, mapControlSettings = MapControlSettings.Builder().build())
    }
    
    private fun createMapEventsTextView() {
        // Convert dp to pixels
        val paddingInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 18f, resources.displayMetrics
        ).toInt()
        
        // Create TextView programmatically
        tvMapEvents = TextView(this)
        tvMapEvents.text = "Map events"
        tvMapEvents.textSize = 18f
        tvMapEvents.setTextColor(android.graphics.Color.RED)
        tvMapEvents.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
        tvMapEvents.setBackgroundColor(android.graphics.Color.WHITE)
        tvMapEvents.visibility = View.GONE // Initially hidden
        
        // Add TextView to the OlaMapView with top gravity
        val layoutParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
        }
        
        mapView.addView(tvMapEvents, layoutParams)
    }

    private fun hideViews(){
        btnTest1.visibility = View.GONE
        btnTest2.visibility = View.GONE
        btnTest3.visibility = View.GONE
        btnTest4.visibility = View.GONE
        btnTest5.visibility = View.GONE
        btnTest6.visibility = View.GONE
        btnTest7.visibility = View.GONE
        btnTest8.visibility = View.GONE
        btnTest9.visibility = View.GONE
        btnTest10.visibility = View.GONE
        tvMapEvents.visibility = View.GONE
    }

    private fun checkRunTimePermission() {

    // Check if the permissions are granted
    val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

    if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        initMap()
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            requestLocationPermission
        )
    }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            requestLocationPermission -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initMap()
                }
            }
        }
    }
}
