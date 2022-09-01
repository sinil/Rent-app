package com.riwal.rentalapp.model

import com.riwal.rentalapp.R

data class InvoiceBody(val InvoiceType: String?,
                       val InvoiceNumber: String?,
                       val OrderNumber: String,
                       val PurchaseOrder: String?,
                       val InvoiceDate: String?,
                       val Amount: Float,
                       val DueDate: String,
                       val Paid: Boolean,
                       val Overdue: Int,
                       val ContactPersonId: String?,
                       val VenueCode: String?) {

    fun toInvoice() = Invoice(
            invoiceType = InvoiceTypes.fromValue(InvoiceType ?: ""),
            invoiceNumber = InvoiceNumber,
            orderNumber = OrderNumber,
            purchaseOrder = PurchaseOrder,
            invoiceDate = InvoiceDate,
            amount = Amount,
            dueDate = DueDate,
            paid = Paid,
            overDue = Overdue,
            contactPersonId = ContactPersonId,
            venueCode = VenueCode
    )


}

data class Invoice(val invoiceType: InvoiceTypes?,
                   val invoiceNumber: String?,
                   val orderNumber: String,
                   val purchaseOrder: String?,
                   val invoiceDate: String?,
                   val amount: Float,
                   val dueDate: String,
                   val paid: Boolean,
                   val overDue: Int,
                   val contactPersonId: String?,
                   val venueCode: String?)

enum class InvoiceTypes(val value: String) {
    SALES(value = "Sales"),
    RENTAL(value = "Rental"),
    MAINTENANCE(value = "Maintenance"),
    DAMAGE(value = "Damage"),
    TRAINING(value = "Training"),
    CREDIT(value = "Credit"),
    HIRE_CREDIT(value = "Hire Credit"),
    REPAIR(value = "Repair"),
    LOSS(value = "Loss"),
    OTHER(value = "Other")
    ;

    companion object {

        fun fromValue(value: String) = values().find { it.value == value }

        val all
            get() = listOf(SALES, RENTAL, MAINTENANCE, DAMAGE, TRAINING, CREDIT, HIRE_CREDIT, REPAIR, LOSS, OTHER)
        val allExceptOther
            get() = listOf(SALES, RENTAL, MAINTENANCE, DAMAGE, TRAINING, CREDIT, HIRE_CREDIT, REPAIR, LOSS)
    }

    val localizedNameRes: Int
        get() = when (this) {
            SALES -> R.string.sales
            RENTAL -> R.string.rental
            MAINTENANCE -> R.string.maintenance
            DAMAGE -> R.string.damage
            TRAINING -> R.string.training
            CREDIT -> R.string.credit
            HIRE_CREDIT -> R.string.hire_credit
            REPAIR -> R.string.repair
            LOSS -> R.string.loss
            OTHER -> R.string.other

        }
}