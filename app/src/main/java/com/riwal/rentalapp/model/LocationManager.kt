package com.riwal.rentalapp.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.location.LocationResult
import com.riwal.rentalapp.model.LocationManager.Accuracy.*


class LocationManager(context: Context) {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null

    var location: Location? = null
        private set

    private val fusedLocationProviderClient = FusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            delegate?.onLocationsUpdated(locationResult.locations)
        }
    }


    /*------------------------------------------ Methods -----------------------------------------*/


    @SuppressLint("MissingPermission")
    fun startUpdatingLocation(desiredAccuracy: Accuracy = BEST) {
        val locationRequest = locationRequestForAccuracy(desiredAccuracy)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stopUpdatingLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun locationRequestForAccuracy(accuracy: Accuracy) = LocationRequest().apply {
        when (accuracy) {
            BEST_FOR_NAVIGATION -> {
                priority = PRIORITY_HIGH_ACCURACY
                interval = 2000

            }
            BEST -> {
                priority = PRIORITY_HIGH_ACCURACY
                interval = 5000
            }
            HUNDRED_METERS -> {
                priority = PRIORITY_BALANCED_POWER_ACCURACY
                interval = 10000
            }
            TEN_KILOMETERS -> {
                priority = PRIORITY_LOW_POWER
                interval = 60000
            }
        }
    }


    /*-------------------------------------- Enums/Classes ---------------------------------------*/


    interface Delegate {
        fun onLocationsUpdated(locations: List<Location>)
    }

    enum class Accuracy {
        BEST_FOR_NAVIGATION,
        BEST,
        HUNDRED_METERS,
        TEN_KILOMETERS
    }

}