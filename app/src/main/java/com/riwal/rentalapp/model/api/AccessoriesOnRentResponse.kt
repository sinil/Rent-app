package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.datetime.toLocalDateTime
import com.riwal.rentalapp.model.*

data class AccessoriesOnRentResponse(
        val SalesId: String,
        val PurchOrderFormNum: String?,
        val AssetOverviewItemId: String,
        val LineDescription: String,
        val DateOnHire: String,
        val DateOffHire: String?,
        val ExpectedDateOffHire: String?,
        val AssetId: String,
        val DeliveryName: String?,
        val DeliveryCity: String?,
        val DeliveryAddress: String?,
        val ProductId: String,
        val OrderStatusId: Int?,
        val ContactPerson: String?,
        val ContactPhone: String?,
        val OperatorNameId: String?,
        val OperatorName: String?,
        val OperatorPhoneNumber: String?,
        val InventTransId: String,
        val PriceRateCode: String,
        val Latitude: Double,
        val Longitude: Double,
        val InspectionDate: String?,
        val ItemName: String
){

    fun toAccessoriesOnRent() = AccessoriesOnRent(
            id = InventTransId,
            rentalType = ProductId,
            brand = AssetOverviewItemId,
            machineType = LineDescription,
            fleetNumber = AssetId,

            orderNumber = SalesId,
            purchaseOrder = PurchOrderFormNum,
            priceRate = PriceRateCode,
            status = RentalStatus.fromValue(OrderStatusId),

            onRentDateTime = DateOnHire.toLocalDateTime()!!,
            offRentDateTime = DateOffHire?.toLocalDateTime() ?: ExpectedDateOffHire?.toLocalDateTime(),
            isOffRentDateFinal = (DateOffHire != null),

            machine = null,
            contact = Contact(
                    name = ContactPerson ?: "",
                    phoneNumber = ContactPhone
            ),
            project = Project(
                    name = DeliveryName,
                    address = DeliveryAddress?.replace("\\s+".toRegex(), " "),
                    contactName = OperatorName,
                    contactPhoneNumber = OperatorPhoneNumber
            ),
            lastInspectionDate = InspectionDate?.toLocalDateTime(),
            accessoryName = ItemName

    )

}