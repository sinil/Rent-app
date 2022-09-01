package com.riwal.rentalapp.model

import com.riwal.rentalapp.R
import org.joda.time.LocalDateTime

data class MyProject(
        val customerId: String,
        val venue: List<VenueBody> = emptyList(),
        val customers: List<customerBody> = emptyList(),
        val machines: List<MachineBody> = emptyList()
)


data class VenueBody(
        val name: String?,
        val city: String?,
        val code: String?
)


data class customerBody(
        val id: String,
        val name: String,
        val companyId: String,
        val databaseName: String,
        val contact: List<Contact>
)


data class MachineBody(
        val inventTransactionId:String?,
        val machineType: String,
        val rentalType: String?,
        val fleetNumber: String,
        val offRentDateTime: LocalDateTime?,
        val onRentDateTime: LocalDateTime?,
        var image: String? = "",
        var status: MachineTransferStatus,
        val rentalOrderNumber: String,
        val contactPersonId: String?,
        val contactPersonName: String?,
        val brand: String,
        val serialNumber: String,
        val orderNumber: String,
        val purchaseOrder: String?,
        val contactPhoneNumber: String?,
        val contactEmail: String?,
        var venueCode: String?
)

enum class MachineTransferStatus(val value: String) {
    TRANSFER_MACHINE("Transferable"),
    PENDING_TRANSFER_MACHINE("PendingTransfer"),
    REQUEST_TRANSFER_MACHINE("Requestable");

    companion object {

        fun fromValue(value: String) = values().find { it.value == value } ?: REQUEST_TRANSFER_MACHINE
    }

    val isPending
        get() = this == PENDING_TRANSFER_MACHINE

    val isTransferable
        get() = this == TRANSFER_MACHINE

    val isRequestable
        get() = this == REQUEST_TRANSFER_MACHINE


    val localizedNameRes: Int
        get() = when (this) {
            REQUEST_TRANSFER_MACHINE -> R.string.request_transfer
            PENDING_TRANSFER_MACHINE -> R.string.pending_transfer
            TRANSFER_MACHINE -> R.string.transfer_machine
        }

}