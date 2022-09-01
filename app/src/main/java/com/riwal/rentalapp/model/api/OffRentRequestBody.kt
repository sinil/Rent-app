package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.api.OffRentRequestBody.MachineBody

data class OffRentRequestBody(
        val customer: CustomerBody,
        val machines: List<MachineBody>,
        val offRentDateTime: String,
        val notes: String,
        val countryCode: Country,
        val pickupLocation: String?
) {

    constructor(customer: Customer, rentals: List<Rental>, offRentDateTime: String, notes: String, countryCode: Country, pickupLocation: String?) : this(
            customer = customer.toCustomerBody(),
            machines = rentals.map { it.toMachineBody() },
            offRentDateTime = offRentDateTime,
            notes = notes,
            countryCode = countryCode,
            pickupLocation = pickupLocation)


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