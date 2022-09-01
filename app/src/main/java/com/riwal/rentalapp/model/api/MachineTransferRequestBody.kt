package com.riwal.rentalapp.model.api


import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Country

class MachineTransferRequestBody(
        val toCustomer: MachineTransferRequestCustomerBody,
        val fromCustomer: MachineTransferRequestCustomerBody,
        val machine: MachineTransferRequestMachinesBody,
        val projectName: String?,
        val transferDate: String?,
        val countryCode: Country

)


data class MachineTransferRequestCustomerBody(
        val id: String,
        val name: String,
        val contact: Contact? = null
)

data class MachineTransferRequestMachinesBody(
        val rentalType: String?,
        val fleetNumber: String?,
        val onHireDate: String?,
        val transactionId: String
)

