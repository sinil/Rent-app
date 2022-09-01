package com.riwal.rentalapp.model

import com.riwal.rentalapp.R
import org.joda.time.LocalDateTime

enum class RentalStatus(val value: Int) {
    UNKNOWN(-1),
    PENDING_DELIVERY(10),
    ON_RENT(20),
    PENDING_PICKUP(30),
    CLOSED(40);

    companion object {

        fun fromValue(value: Int?) = values().find { it.value == value } ?: UNKNOWN

        val allExceptUnknown
            get() = listOf(PENDING_DELIVERY, ON_RENT, PENDING_PICKUP, CLOSED)
    }

    val isClosed
        get() = this == CLOSED

    val localizedNameRes: Int
        get() = when (this) {
            UNKNOWN -> R.string.order_status_unknown
            PENDING_DELIVERY -> R.string.order_status_pending_deliveries
            ON_RENT -> R.string.order_status_current_rentals
            PENDING_PICKUP -> R.string.order_status_pending_pickup
            CLOSED -> R.string.order_status_closed_rentals
        }
}

data class Rental(
        val id: String,
        val rentalType: String,
        val brand: String,
        val machineType: String,
        val fleetNumber: String,
        val itemName: String?,
        val orderNumber: String,
        val purchaseOrder: String?,
        val priceRate: String,
        val status: RentalStatus,

        val onRentDateTime: LocalDateTime,
        val offRentDateTime: LocalDateTime?,
        val isOffRentDateFinal: Boolean,

        var machineCoordinate: Coordinate?,

        val machine: Machine?,
        val contact: Contact,
        val project: Project,

        val lastInspectionDate: LocalDateTime?,
        val orderedById: String?,
        val orderedByName: String?
)
