package com.riwal.rentalapp.model

data class RentalDetail(
        val id: String? = "",
        val rentalType: String? = "",
        val brand: String? = "",
        val machineType: String? = "",
        val fleetNumber: String? = "",
        val itemRequested: String? = "",
        val itemReceived: String? = "",
        val requestedMachine: Machine? = null
)