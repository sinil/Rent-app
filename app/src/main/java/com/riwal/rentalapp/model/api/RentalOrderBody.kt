package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.datetime.isoString
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.EXACT

data class RentalOrderBody(
        val customer: CustomerBody? = null,
        val contact: Contact,
        val project: Project,
        val machines: List<MachineOrder>,
        val purchaseOrder: String? = null,
        val comments: String? = null,
        val countryCode: Country) {

    data class MachineOrder(
            val rentalType: String?,
            val onRentDate: String?,
            val deliveryTime: String?,
            val offRentDate: String?,
            val offRentTime: String?,
            val offRentDateIsFinal: Boolean,
            val quantity: Int,
            val accessories: List<AccessoryOrderBody>
    )

}

fun MachineOrder.toMachineOrderBody() = RentalOrderBody.MachineOrder(
        rentalType = machine.rentalType,
        onRentDate = onRentDate.isoString,
        deliveryTime = deliveryTime.isoString,
        offRentDate = offRentDate.isoString,
        offRentTime = if (offRentDateStrictness == EXACT) offRentTime!!.isoString else null,
        offRentDateIsFinal = offRentDateStrictness == EXACT,
        quantity = quantity,
        accessories = accessories.filter { it.quantity >0 }.map {it.toAccessoryOrderBody() }
)