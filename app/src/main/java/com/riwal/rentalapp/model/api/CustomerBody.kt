package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Customer

data class CustomerBody(
        val id: String,
        val name: String
)

fun Customer.toCustomerBody() = CustomerBody(
        id = if(companyId == "15") newId else id,
        name = name
)