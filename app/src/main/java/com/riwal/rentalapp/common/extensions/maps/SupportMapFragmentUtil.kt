package com.riwal.rentalapp.common.extensions.maps

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun SupportMapFragment.getMap() = suspendCoroutine<GoogleMap> { c ->
    getMapAsync { map -> c.resume(map) }
}