package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.api.CancelAccessoriesRequestBody.AccessoriesBody

data class CancelAccessoriesRequestBody(
        val customer: CustomerBody,
        val accessories: List<AccessoriesBody>,
        val onRentDateTime: String,
        val notes: String,
        val countryCode: Country
) {

    constructor(customer: Customer, accessories: List<AccessoriesOnRent>, onRentDateTime: String, notes: String, countryCode: Country) : this(
            customer = customer.toCustomerBody(),
            accessories = accessories.map { it.toAccessoriesBody() },
            onRentDateTime = onRentDateTime,
            notes = notes,
            countryCode = countryCode)


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