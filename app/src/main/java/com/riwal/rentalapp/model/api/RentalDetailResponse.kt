package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.RentalDetail


data class RentalDetailResponse(
        val InventTransId: String,
        val AssetOverviewItemId: String,
        val ProductId: String,
        val LineDescription: String,
        val AssetId: String,
        val ItemRequested: String,
        val ItemReceived: String
) {
    fun toRentalDetail() = RentalDetail(
            id = InventTransId,
            rentalType = ProductId,
            brand = AssetOverviewItemId,
            machineType = LineDescription,
            fleetNumber = AssetId,
            itemRequested = ItemRequested,
            itemReceived = ItemReceived,
            requestedMachine = null
    )
}
