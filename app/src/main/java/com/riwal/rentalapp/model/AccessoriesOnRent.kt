package com.riwal.rentalapp.model

import org.joda.time.LocalDateTime

data class AccessoriesOnRent(
        val id: String,
        val accessoryName: String,
        val rentalType: String,
        val brand: String,
        val machineType: String,
        val fleetNumber: String,

        val orderNumber: String,
        val purchaseOrder: String?,
        val priceRate: String,
        val status: RentalStatus,

        val onRentDateTime: LocalDateTime,
        val offRentDateTime: LocalDateTime?,
        val isOffRentDateFinal: Boolean,

        val machine: Machine?,
        val contact: Contact,
        val project: Project,

        val lastInspectionDate: LocalDateTime?
)