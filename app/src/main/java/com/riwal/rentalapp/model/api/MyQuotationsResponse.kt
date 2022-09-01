package com.riwal.rentalapp.model.api

data class MyQuotationsResponse(
        val ContactPersons: List<ContactResponse>?,
        val Quotations: List<QuotationsResponse>?,
        val Venues: List<VenueResponse>?
) {


}