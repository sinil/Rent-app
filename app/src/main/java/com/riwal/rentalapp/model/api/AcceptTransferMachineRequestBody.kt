package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country

class AcceptTransferMachineRequestBody(
        val transferDate: String,
        val userName: String,
        val transferId:String,
        val fromCustomer: acceptCustomersRequestBody,
        val toCustomer: acceptCustomersRequestBody,
        val machine: acceptMachineRequestBody,
        val countryCode: Country,
        var photos: MutableList<String> = ArrayList()
) {

    data class acceptCustomersRequestBody(
            val companyId: String,
            val id: String,
            val name: String,
            val contact: acceptContactRequestBody?
    )

    data class acceptContactRequestBody(
            val name: String?,
            val phone:String?,
            val email:String?
    )

    data class acceptMachineRequestBody(
            val rentalType: String?,
            val brand: String?,
            val fleetNumber: String?,
            val serialNumber: String?,
            val onHireDate: String?,
            val offHireDate: String?,
            val orderNumber:String,
            val purchaseOrder:String

    )
}