package com.riwal.rentalapp.placepicker

import android.location.Geocoder
import android.util.Log
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Coordinate
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Place
import kotlinx.coroutines.*
import java.io.IOException

class PlacePickerController(val view: PlacePickerView, val geocoder: Geocoder, val activeCountry: Country) : ViewLifecycleObserver, PlacePickerView.Delegate, PlacePickerView.DataSource, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null

    private var selectedPlace: Place? = null
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private var automaticallyUpdatePlaceWithUserLocation = true


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*------------------------------------- PlacePicker DataSource --------------------------------------*/


    override fun activeCountry(view: PlacePickerView) = activeCountry
    override fun selectedPlace(view: PlacePickerView) = selectedPlace


    /*------------------------------------- PlacePicker Delegate --------------------------------------*/


    override fun onNavigateBackSelected(view: PlacePickerView) = view.navigateBack()
    override fun onSearchPlaceSelected(view: PlacePickerView) = view.navigateToSearchPlacePage()

    override fun onUserLocationUpdated(view: PlacePickerView, coordinate: Coordinate) {
        if (automaticallyUpdatePlaceWithUserLocation) {
            reverseGeocode(coordinate)
            view.moveToLocation(coordinate)
        }
    }

    override fun onLocationPickedByDragging(view: PlacePickerView, coordinate: Coordinate) {
        automaticallyUpdatePlaceWithUserLocation = false
        reverseGeocode(coordinate)
    }

    override fun onPlacePickerBySearching(view: PlacePickerView, place: Place) {
        automaticallyUpdatePlaceWithUserLocation = false
        this.selectedPlace = place
        view.moveToLocation(place.coordinate)
    }

    override fun onSelectThisLocationSelected(view: PlacePickerView) {
        if (selectedPlace == null) {
            return
        }

        delegate?.onPlacePicked(controller = this, place = selectedPlace!!)
        view.navigateBack()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun reverseGeocode(coordinate: Coordinate) = launch {
        try {

            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
            }
            val address = addresses.firstOrNull() ?: return@launch

            selectedPlace = Place(address = address.getAddressLine(0) ?: "${address.latitude}, ${address.longitude}", coordinate = coordinate)

        } catch (e: IOException) {
            // TODO: Do something with the error
            Log.e("PlacePickerViewImpl", e.localizedMessage)
        }
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onPlacePicked(controller: PlacePickerController, place: Place)
    }


}