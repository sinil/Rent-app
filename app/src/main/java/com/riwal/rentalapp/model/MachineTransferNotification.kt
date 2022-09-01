package com.riwal.rentalapp.model

data class MachineTransferNotification(
        val transferId: String,
        val text: String?,
        var status: MachineTransferNotificationStatus?,
        val machine: MachineBody?,
        val fromCustomer: TransferNotificationCustomer,
        val toCustomer: TransferNotificationCustomer

) {


    data class TransferNotificationCustomer(
            val companyId:String,
            val customerId:String,
            val name:String,
            val databaseName:String,
            val contact: Contact?
    )

}

enum class MachineTransferNotificationStatus(val value: Int) {
    UNKNOWN(50),
    TRANSFER_RECEIVED(20),
    TRANSFER_ACCEPTED(30),
    TRANSFER_REQUESTED(10);

    companion object {

        fun fromValue(value: Int?) = values().find { it.value == value } ?: UNKNOWN
    }


}

