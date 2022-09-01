package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer

data class CustomerResponse(val DatabaseName: String, val CompanyId: String,
                            val CustomerId: String, val Name: String, val City: String,
                            val BreakDownEmailAddress: String?, val InspHireAcct: String) {

    fun toCustomer() = Customer(id = CustomerId, name = Name, companyId = CompanyId,
        databaseName = DatabaseName, canReportBreakdown = BreakDownEmailAddress != null, newId = InspHireAcct)

}
