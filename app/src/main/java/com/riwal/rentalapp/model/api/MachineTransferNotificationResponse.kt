package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.MachineTransferNotification
import com.riwal.rentalapp.model.MachineTransferNotificationStatus

class MachineTransferNotificationResponse(
        val TransferId: String,
        val text: String?,
        val Notification_Status: Int?,
        val Machine: MachineTransferResponse?,
        val FromCustomer: TransferNotificationCustomerBody,
        val ToCustomer: TransferNotificationCustomerBody

) {
    fun toTransferNotification() = MachineTransferNotification(
            transferId = TransferId,
            text = text,
            status = MachineTransferNotificationStatus.fromValue(Notification_Status),
            machine = Machine?.toMachineResponse(),
            fromCustomer = FromCustomer.toTransferCustomer(),
            toCustomer = ToCustomer.toTransferCustomer()
    )


    data class TransferNotificationCustomerBody(
            val CompanyId: String,
            val CustomerId: String,
            val CustomerName: String,
            val DatabaseName:String,
            val Contacts: ContactResponse?
    ) {
        fun toTransferCustomer() = MachineTransferNotification.TransferNotificationCustomer(
                companyId = CompanyId,
                customerId = CustomerId,
                name = CustomerName,
                databaseName = DatabaseName,
                contact = Contacts?.toContactBody()
        )
    }


    data class ContactResponse(
            val ContactPerson: String?,
            val ContactNumber: String?,
            val ContactEmail: String?,
            val ContactPersonId:String?
    ) {
        fun toContactBody(): Contact? {
            return Contact(
                    name = ContactPerson!!,
                    phoneNumber = ContactNumber,
                    email = ContactEmail!!,
                    id = ContactPersonId
            )
        }
    }

}