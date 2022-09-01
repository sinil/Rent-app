package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.VenueBody

data class VenueResponse(
        val DeliveryName: String?,
        val DeliveryCity: String?,
        val RorVenueCode: String?
){

fun toVenue(): VenueBody? {
    return VenueBody(
            name = DeliveryName,
            city = DeliveryCity,
            code = RorVenueCode
    )
}

}