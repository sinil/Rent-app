package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.Rental

class BreakdownRequestBody(
        val customer: CustomerBody,
        val machine: MachineBreakdownBody,
        val breakdownMessage: String,
        val countryCode: Country,
        val companyName: String,
        val contactPhoneNumber: String?) {

    constructor(customer: Customer, rental: Rental, breakdownMessage: String, countryCode: Country, companyName: String, contactPhoneNumber: String?) : this(
            customer = customer.toCustomerBody(),
            machine = rental.toMachineBreakdownBody(),
            breakdownMessage = breakdownMessage,
            countryCode = countryCode,
            companyName = companyName,
            contactPhoneNumber = contactPhoneNumber)


    data class MachineBreakdownBody(
            val fleetNumber: String,
            val rentalType: String,
            val type: String,
            val projectName: String?
    )

}

private fun Rental.toMachineBreakdownBody() = BreakdownRequestBody.MachineBreakdownBody(
        fleetNumber = fleetNumber,
        rentalType = rentalType,
        type = machineType,
        projectName = project.name
)