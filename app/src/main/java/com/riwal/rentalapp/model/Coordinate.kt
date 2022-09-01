package com.riwal.rentalapp.model

import com.google.android.gms.maps.model.LatLng

data class Coordinate(val latitude: Double = 0.0, val longitude: Double = 0.0)

fun LatLng.toCoordinate() = Coordinate(latitude = latitude, longitude = longitude)

fun Coordinate.toLatLng() = LatLng(latitude, longitude)