package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.datetime.toLocalDateTime
import com.riwal.rentalapp.model.*


data class MyProjectResponse(
        val CustomerId: String,
        val Venues: List<VenueResponseBody>,
        val Customers: List<CustomerMachineTransferResponse>,
        val Machines: List<MachineTransferResponse>
) {


    fun toMyProject(): MyProject? {
        return MyProject(customerId = CustomerId,
                venue = Venues.mapNotNull { it.toVenueBody() },
                customers = Customers.mapNotNull { it.toCustomerBody() },
                machines = Machines.mapNotNull { it.toMachineResponse() })
    }


}

data class VenueResponseBody(
        val DeliveryName: String,
        val DeliveryCity: String,
        val RorVenueCode: String
) {
    fun toVenueBody(): VenueBody? {
        return VenueBody(
                name = DeliveryName,
                city = DeliveryCity,
                code = RorVenueCode
        )
    }
}


data class CustomerMachineTransferResponse(
        val CustomerId: String,
        val CustomerName: String,
        val CompanyId: String,
        val DatabaseName: String,
        val Contacts: List<ContactResponse>?
) {
    fun toCustomerBody(): customerBody? {
        return customerBody(
                id = CustomerId,
                name = CustomerName,
                companyId = CompanyId,
                databaseName = DatabaseName,
                contact = Contacts?.mapNotNull { it.toContactBody() } ?: emptyList()
        )
    }
}


data class ContactResponse(
        val ContactPersonId: String?,
        val ContactPerson: String?,
        val ContactPersonName: String?,
        val ContactNumber: String?,
        val ContactEmail: String?
) {
    fun toContactBody(): Contact? {
        return Contact(
                id = ContactPersonId,
                name = ContactPerson ?: ContactPersonName,
                phoneNumber = ContactNumber,
                email = ContactEmail
        )
    }
}

data class MachineTransferResponse(
        val InventTransanctionId: String?,
        val ContactPersonId: String?,
        val ContactPersonName: String?,
        val MachineType: String?,
        val FleetNumber: String?,
        val OffHireDate: String?,
        val OnHireDate: String?,
        val MachineStatus: String,
        val RentalOrderNumber: String?,
        val expectedDateOffHire: String?,
        val ProductId: String?,
        val AssetOverviewId: String?,
        val SerialNumber: String?,
        val OrderNumber: String?,
        val PurchaseOrder: String?,
        val ContactPhoneNumber: String?,
        val ContactEmail: String?,
        val RorVenueCode: String
) {
    fun toMachineResponse(): MachineBody? {
        return MachineBody(
                inventTransactionId = InventTransanctionId,
                contactPersonId = ContactPersonId,
                contactPersonName = ContactPersonName,
                machineType = MachineType!!,
                rentalType = ProductId,
                fleetNumber = FleetNumber!!,
                onRentDateTime = OnHireDate?.toLocalDateTime(),
                offRentDateTime = OffHireDate?.toLocalDateTime(),
                status = MachineTransferStatus.fromValue(MachineStatus),
                rentalOrderNumber = RentalOrderNumber!!,
                brand = AssetOverviewId!!,
                serialNumber = SerialNumber!!,
                orderNumber = OrderNumber!!,
                purchaseOrder = PurchaseOrder,
                contactPhoneNumber = ContactPhoneNumber,
                contactEmail = ContactEmail,
                venueCode = RorVenueCode
        )
    }
}



