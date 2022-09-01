package com.riwal.rentalapp.placepicker

import android.annotation.SuppressLint
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete
import com.google.android.material.snackbar.Snackbar
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.ActivityResultException
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.extensions.core.doAsync
import com.riwal.rentalapp.common.ui.mapview.MapView
import com.riwal.rentalapp.common.ui.mapview.MapView.CameraMoveCause.GESTURE
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.model.Coordinate
import com.riwal.rentalapp.model.Place
import com.riwal.rentalapp.model.toCoordinate
import com.riwal.rentalapp.model.toLatLng
import com.riwal.rentalapp.model.util.bounds
import kotlinx.android.synthetic.main.view_place_picker.*
import kotlinx.coroutines.rx2.await


open class PlacePickerViewImpl : RentalAppNotificationActivity(), MapView.Delegate, PlacePickerView {


    /*---------------------------------------- Properties ----------------------------------------*/

    override lateinit var dataSource: PlacePickerView.DataSource
    override lateinit var delegate: PlacePickerView.Delegate

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val pickedPlace
        get() = dataSource.selectedPlace(view = this)

    private val defaultZoom = 16f


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_place_picker)

        mapView.delegate = this
        mapView.onCameraMovedListener = { cameraPosition, cause ->
            if (cause == GESTURE) {
                delegate.onLocationPickedByDragging(view = this, coordinate = cameraPosition.target.toCoordinate())
            }
        }

        addressCardView.setOnClickListener { delegate.onSelectThisLocationSelected(view = this) }
        searchCardView.setOnClickListener { delegate.onSearchPlaceSelected(view = this) }

        updateUI()
    }

    override fun onBackPressed() {
        delegate.onNavigateBackSelected(view = this)
    }

    override fun updateUI(animated: Boolean) {
        addressTextView.text = pickedPlace?.address ?: getString(R.string.no_location_selected)
    }


    /*------------------------------------- PlacePickerView --------------------------------------*/


    override fun navigateBack() = finish(transition = ModalPopActivityTransition)

    @SuppressLint("CheckResult")
    override fun navigateToSearchPlacePage() = doAsync {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this@PlacePickerViewImpl)
            val autocompletePlace = startActivityForResult(intent).await()
            val place = PlaceAutocomplete.getPlace(this@PlacePickerViewImpl, autocompletePlace)?.toPlace() ?: return@doAsync
            delegate.onPlacePickerBySearching(view = this@PlacePickerViewImpl, place = place)
        } catch (error: Exception) {
            if (error !is ActivityResultException) {
                Snackbar.make(contentView, error.localizedMessage, Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    override fun moveToLocation(coordinate: Coordinate) {
        mapView.zoomToLocation(coordinate.toLatLng(), defaultZoom)
    }

    /*------------------------------------ MapView Delegate --------------------------------------*/


    override fun onMapViewFinishedLoading(mapView: MapView) {
        updateUI()
    }

    override fun onLocationPermissionUpdated(granted: Boolean) {
        if (!granted) {
            mapView.zoomToBounds(activeCountry.bounds, true)
        }
    }

    override fun onUserLocationUpdated(userLocation: LatLng) = delegate.onUserLocationUpdated(view = this, coordinate = userLocation.toCoordinate())


    /*---------------------------------------- Extensions ----------------------------------------*/


    private fun com.google.android.libraries.places.compat.Place.toPlace() = Place(
            address = address.toString(),
            coordinate = latLng.toCoordinate()
    )

}