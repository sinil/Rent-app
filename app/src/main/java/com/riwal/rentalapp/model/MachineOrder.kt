package com.riwal.rentalapp.model

import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class MachineOrder(
        val machine: Machine,
        var quantity: Int = 1,
        var onRentDate: LocalDate,
        var deliveryTime: LocalTime,
        var offRentDate: LocalDate,
        var offRentTime: LocalTime? = null,
        var offRentDateStrictness: OffRentDateStrictness,
        var accessories: MutableList<AccessoryOrder> = mutableListOf()
) {
    enum class OffRentDateStrictness {
        UNSPECIFIED,
        EXACT,
        APPROXIMATE
    }
}