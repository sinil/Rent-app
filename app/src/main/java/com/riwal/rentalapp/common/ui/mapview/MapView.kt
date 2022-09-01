package com.riwal.rentalapp.common.ui.mapview

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.*
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.Insets
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.graphics.replace
import com.riwal.rentalapp.common.extensions.android.hasPermission
import com.riwal.rentalapp.common.extensions.android.requestPermission
import com.riwal.rentalapp.common.extensions.android.supportFragmentManager
import com.riwal.rentalapp.common.extensions.core.doAsync
import com.riwal.rentalapp.common.extensions.maps.*
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.mapview.MapView.CameraMoveCause.*
import com.riwal.rentalapp.model.LocationManager
import com.riwal.rentalapp.model.LocationManager.Accuracy.BEST_FOR_NAVIGATION
import com.riwal.rentalapp.model.RentalManager
import kotlinx.android.synthetic.main.view_map_container.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.rx2.await

class MapView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), LocationManager.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null

    var autoRequestLocationPermissionIfNeeded = true

    var userLocation: LatLng? = null
        private set(value) {
            field = value
            if (value != null) {
                delegate?.onUserLocationUpdated(value)
            }
        }

    var showUserLocation = false
        set(value) {
            field = value
            enableUserLocationUpdates(enable = value)
        }

    private var useLiteMode = false

    var contentInsets = Insets(left = 0, top = 0, right = 0, bottom = 0)
        set(value) {
            field = value
            googleMap?.setContentInsets(value)
        }

    private var _clusterItems: List<ClusterItem> = emptyList()
    val clusterItems
        get() = _clusterItems + listOfNotNull(userLocationClusterItem)

    var onCameraMovedListener: ((cameraPosition: CameraPosition, cause: CameraMoveCause) -> Unit)? = null

    val hasFinishedLoading
        get() = googleMap != null

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<ClusterItem>? = null
    private var locationManager = LocationManager(context)

    private val userLocationClusterItem
        get() = if (userLocation != null) UserLocationClusterItem(position = userLocation!!) else null

    private var cameraMoveReason = PROGRAMMATICALLY

    private lateinit var mapFragment: SupportMapFragment


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        addSubview(R.layout.view_map_container)

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.MapView, 0, 0)
            val contentInsetLeft = styledAttributes.getDimensionPixelOffset(R.styleable.MapView_contentInsetLeft, 0)
            val contentInsetTop = styledAttributes.getDimensionPixelOffset(R.styleable.MapView_contentInsetTop, 0)
            val contentInsetRight = styledAttributes.getDimensionPixelOffset(R.styleable.MapView_contentInsetRight, 0)
            val contentInsetBottom = styledAttributes.getDimensionPixelOffset(R.styleable.MapView_contentInsetBottom, 0)
            contentInsets = Insets(contentInsetLeft, contentInsetTop, contentInsetRight, contentInsetBottom)
            showUserLocation = styledAttributes.getBoolean(R.styleable.MapView_showUserLocation, false)
            useLiteMode = styledAttributes.getBoolean(R.styleable.MapView_useLiteMode, false)
            styledAttributes.recycle()
        }

        locationManager.delegate = this

        setupGoogleMapFragment()
        setupGoogleMap()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun addMarker(markerOptions: MarkerOptions) = googleMap?.addMarker(markerOptions)

    fun setInfoWindowAdapter(infoWindowAdapter: InfoWindowAdapter) = googleMap?.setInfoWindowAdapter(infoWindowAdapter)

    fun setClusterItems(items: List<ClusterItem>) {
        _clusterItems = items.filter { it !is UserLocationClusterItem }
        clusterManager?.setItems(_clusterItems)
    }

    fun zoomToShowClusterItems(clusterItems: List<ClusterItem>) {
        val locations = clusterItems.map { it.position }
        googleMap?.zoomToBoundsOfLocations(locations)
    }

    fun zoomToLocation(latLng: LatLng, zoom: Float, animated: Boolean = true) {

        if (googleMap?.cameraPosition?.target == latLng) {
            return
        }

        googleMap?.zoomToLocation(latLng, zoom, animated)
    }

    fun zoomToBounds(bounds: LatLngBounds, animated: Boolean = true) = googleMap?.zoomToBounds(bounds, animated)

    fun calculateVisibleBounds() = googleMap?.projection?.visibleRegion?.latLngBounds


    /*------------------------------------- LocationManager --------------------------------------*/


    override fun onLocationsUpdated(locations: List<Location>) {
        val lastLocation = locations.lastOrNull()
        if (lastLocation != null) {
            userLocation = lastLocation.toLatLng()
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun setupGoogleMapFragment() {
        val googleMapOptions = GoogleMapOptions().apply {
            liteMode(useLiteMode)
        }
        mapFragment = SupportMapFragment.newInstance(googleMapOptions)
        supportFragmentManager.replace(R.id.mapFragmentContainer, mapFragment)
    }

    private fun setupGoogleMap() = doAsync(Main) {

        googleMap = mapFragment.getMap()
        delegate?.onMapViewFinishedLoading(this@MapView)
        googleMap!!.setOnMapLoadedCallback { delegate?.onMapViewFinishedRendering(this@MapView) }

        clusterManager = ClusterManager<ClusterItem>(context, googleMap).apply {
            setOnClusterItemInfoWindowClickListener { clusterItem -> delegate?.onClusterItemInfoWindowSelected(mapView = this@MapView, clusterItem = clusterItem) }
            setOnClusterClickListener { cluster -> delegate?.onClusterSelected(mapView = this@MapView, cluster = cluster); true }
            setItems(_clusterItems)
        }

        googleMap!!.apply {
            setContentInsets(contentInsets)
            setOnMarkerClickListener { marker -> onMarkerSelected(marker) }
            setOnInfoWindowClickListener { marker -> onInfoWindowSelected(marker) }
            setOnCameraIdleListener { onCameraIdled() }
            setOnCameraMoveStartedListener { reason -> onCameraMoved(reason) }
        }

        enableUserLocationUpdates(showUserLocation)

    }

    private fun enableUserLocationUpdates(enable: Boolean) {
        if (enable) {
            startUserLocationUpdatesIfPossible()
        } else {
            stopUserLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUserLocationUpdatesIfPossible() {

        if (!hasPermission(ACCESS_FINE_LOCATION) && !autoRequestLocationPermissionIfNeeded) {
            return
        }

        doAsync {

            if (googleMap == null || googleMap!!.isMyLocationEnabled) {
                return@doAsync
            }

            val locationPermissionGranted = requestPermission(ACCESS_FINE_LOCATION).await()

            delegate?.onLocationPermissionUpdated(granted = locationPermissionGranted)

            if (locationPermissionGranted) {
                googleMap!!.isMyLocationEnabled = true
                locationManager.startUpdatingLocation(desiredAccuracy = BEST_FOR_NAVIGATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopUserLocationUpdates() {
        googleMap?.isMyLocationEnabled = false
        locationManager.stopUpdatingLocation()
        userLocation = null
    }

    private fun onMarkerSelected(marker: Marker) = when {
        marker.isClusterItem -> clusterManager!!.onMarkerClick(marker)
        else -> delegate?.onMarkerSelected(mapView = this, marker = marker) ?: true
    }

    private fun onInfoWindowSelected(marker: Marker) = when {
        marker.isClusterItem -> clusterManager!!.onInfoWindowClick(marker)
        else -> delegate?.onMarkerInfoWindowSelected(mapView = this, marker = marker)
    }

    private fun onCameraMoved(reason: Int) {
        cameraMoveReason = when (reason) {
            REASON_GESTURE -> GESTURE
            else -> PROGRAMMATICALLY
        }
    }

    private fun onCameraIdled() {
        clusterManager!!.onCameraIdle()
        onCameraMovedListener?.invoke(googleMap!!.cameraPosition, cameraMoveReason)
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    enum class CameraMoveCause {
        PROGRAMMATICALLY,
        GESTURE
    }

    interface Delegate {

        fun onMapViewFinishedLoading(mapView: MapView) {}
        fun onMapViewFinishedRendering(mapView: MapView) {}
        fun onLocationPermissionUpdated(granted: Boolean) {}
        fun onUserLocationUpdated(userLocation: LatLng) {}
        fun onClusterSelected(mapView: MapView, cluster: Cluster<ClusterItem>) {}
        fun onClusterItemInfoWindowSelected(mapView: MapView, clusterItem: ClusterItem) {}
        fun onMarkerSelected(mapView: MapView, marker: Marker): Boolean {
            return true
        }

        fun onMarkerInfoWindowSelected(mapView: MapView, marker: Marker) {}

    }


    /*---------------------------------------- Extensions ----------------------------------------*/


    private val Marker.isClusterItem
        get() = position in _clusterItems.map { it.position }

}
