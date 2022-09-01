package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.datetime.toLocalDateTime
import com.riwal.rentalapp.model.Quotation

data class QuotationsResponse(
        var QuotationNo: String?,
        var CustomerOrderRefNo: String?,
        val QuotationDate: String?,
        var QuotationStatus: String?,
        var CustomerId: String?,
        var OnHireDate: String?,
        var OffHireDate: String?,
        var ContactPerson: String?,
        var VenueCode: String?,
        var VenueName: String?,
        var VenueCity: String?,
        var QuotationTotal: String?
){
fun toQuotations(): Quotation? {
    return Quotation(
            quotationNo = QuotationNo,
            customerOrderRefNo = CustomerOrderRefNo,
            quotationDate = QuotationDate?.toLocalDateTime(),
            quotationStatus = QuotationStatus,
            customerId = CustomerId,
            onHireDate = OnHireDate?.toLocalDateTime(),
            offHireDate = OffHireDate?.toLocalDateTime(),
            contactPerson = ContactPerson,
            venueCode = VenueCode,
            venueName = VenueName,
            venueCity = VenueCity,
            quotationTotal = QuotationTotal
    )
}
}