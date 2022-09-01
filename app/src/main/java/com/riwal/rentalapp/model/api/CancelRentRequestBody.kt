package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.api.CancelRentRequestBody.MachineBody

data class CancelRentRequestBody(
        val customer: CustomerBody,
        val machines: List<MachineBody>,
        val onRentDateTime: String,
        val notes: String,
        val countryCode: Country
) {

    constructor(customer: Customer, rentals: List<Rental>, onRentDateTime: String, notes: String, countryCode: Country) : this(
            customer = customer.toCustomerBody(),
            machines = rentals.map { it.toMachineBody() },
            onRentDateTime = onRentDateTime,
            notes = notes,
            countryCode = countryCode)


    data class MachineBody(
            val fleetNumber: String,
            val rentalType: String,
            val orderNumber: String,
            val type: String,
            val image: String?,
            val projectName: String?
    )

}

private fun Rental.toMachineBody() = MachineBody(
        fleetNumber = fleetNumber,
        rentalType = rentalType,
        orderNumber = orderNumber,
        type = machineType,
        image = machine?.thumbnailUrl,
        projectName = project.name
)