package com.riwal.rentalapp.common.extensions.maps

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.riwal.rentalapp.common.Insets

fun GoogleMap.zoomToBoundsOfLocations(locations: List<LatLng>, animated: Boolean = true) {

    if (locations.isEmpty()) {
        return
    }

    try {
        val boundsForFirstItem = LatLngBounds(locations.first(), width = .0001, height = .0001)
        val bounds = locations.fold(boundsForFirstItem) { bounds, latLng -> bounds.including(latLng) }
        zoomToBounds(bounds, animated)
    } catch (exception: Exception) {
        // Ignore
    }

}

fun GoogleMap.zoomToBounds(bounds: LatLngBounds, animated: Boolean = true) {
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0)
    if (animated) {
        animateCamera(cameraUpdate)
    } else {
        moveCamera(cameraUpdate)
    }
}

fun GoogleMap.zoomToLocation(latLng: LatLng, zoom: Float, animated: Boolean = true) {
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
    if (animated) {
        animateCamera(cameraUpdate)
    } else {
        moveCamera(cameraUpdate)
    }
}

fun GoogleMap.setContentInsets(insets: Insets) = setPadding(insets.left, insets.top, insets.right, insets.bottom)
