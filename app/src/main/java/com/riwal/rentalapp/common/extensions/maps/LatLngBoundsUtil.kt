package com.riwal.rentalapp.common.extensions.maps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun LatLngBounds(center: LatLng, width: Double, height: Double) = LatLngBounds(
        LatLng(center.latitude - width / 2, center.longitude - height / 2),
        LatLng(center.latitude + width / 2, center.longitude + height / 2)
)