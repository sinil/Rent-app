package com.riwal.rentalapp.placepicker

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Coordinate
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Place

interface PlacePickerView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToSearchPlacePage()
    fun moveToLocation(coordinate: Coordinate)

    interface DataSource {
        fun activeCountry(view: PlacePickerView): Country
        fun selectedPlace(view: PlacePickerView): Place?
    }

    interface Delegate {
        fun onNavigateBackSelected(view: PlacePickerView)
        fun onSearchPlaceSelected(view: PlacePickerView)
        fun onUserLocationUpdated(view: PlacePickerView, coordinate: Coordinate)
        fun onPlacePickerBySearching(view: PlacePickerView, place: Place)
        fun onLocationPickedByDragging(view: PlacePickerView, coordinate: Coordinate)
        fun onSelectThisLocationSelected(view: PlacePickerView)
    }

}