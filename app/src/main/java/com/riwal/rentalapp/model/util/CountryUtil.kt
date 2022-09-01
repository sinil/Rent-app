package com.riwal.rentalapp.model.util

import com.google.android.gms.maps.model.LatLngBounds
import com.riwal.rentalapp.common.ui.mapview.CountryBounds
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Country.CORPORATE

val Country.bounds: LatLngBounds
    get() {
        return if (this == CORPORATE) {
            // TODO: Should zoom to all countries, but doesn't seem to work.
            Country.countriesForCurrentBrand
                    .filter { it != CORPORATE }
                    .map { it.bounds }
                    .reduce { acc, latLngBounds -> acc union latLngBounds }
        } else {
            CountryBounds.valueOf(name).bounds
        }
    }

infix fun LatLngBounds.union(other: LatLngBounds): LatLngBounds = this
        .including(other.northeast)
        .including(other.southwest)
