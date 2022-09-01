package com.riwal.rentalapp.model

import org.joda.time.LocalDateTime


data class Quotation(
        var quotationNo: String?,
        var customerOrderRefNo: String?,
        val quotationDate: LocalDateTime?,
        var quotationStatus: String?,
        var customerId: String?,
        var onHireDate: LocalDateTime?,
        var offHireDate: LocalDateTime?,
        var contactPerson: String?,
        var venueCode: String?,
        var venueName: String?,
        var venueCity: String?,
        var quotationTotal: String?
)