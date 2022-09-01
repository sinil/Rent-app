package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.api.OffRentAccessoriesRequestBody.AccessoriesBody


class OffRentAccessoriesRequestBody (
        val customer: CustomerBody,
        val accessories: List<AccessoriesBody>,
        val offRentDateTime: String,
        val notes: String,
        val countryCode: Country,
        val pickupLocation : String?
) {

    constructor(customer: Customer, accessories: List<AccessoriesOnRent>, offRentDateTime: String, notes: String, countryCode: Country,pickupLocation: String?): this (
            customer = customer.toCustomerBody(),
            accessories = accessories.map { it.toAccessoriesBody() },
            offRentDateTime = offRentDateTime,
            notes = notes,
            countryCode = countryCode,
            pickupLocation = pickupLocation)

    data class AccessoriesBody(
            val accessoryName: String,
            val fleetNumber: String,
            val rentalType: String,
            val orderNumber: String,
            val type: String,
            val projectName: String?
    )


}

private fun AccessoriesOnRent.toAccessoriesBody() = AccessoriesBody(
        accessoryName = accessoryName,
        fleetNumber = fleetNumber,
        rentalType = rentalType,
        orderNumber = orderNumber,
        type = machineType,
        projectName = project.name
)