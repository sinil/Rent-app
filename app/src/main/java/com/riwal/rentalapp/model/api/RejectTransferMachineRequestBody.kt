package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Country

class RejectTransferMachineRequestBody(
        val toCustomer: CustomerRejectTransferBody?,
        val fromCustomer: CustomerRejectTransferBody?,
        val machine: MachineRejectTransferBody,
        val transferId: String,
        val countryCode: Country
) {
    data class CustomerRejectTransferBody(val id: String?, val name: String?, val contact: Contact?)
    data class MachineRejectTransferBody(val rentalType: String, val fleetNumber: String, val onHireDate: String, val offHireDate: String)
}