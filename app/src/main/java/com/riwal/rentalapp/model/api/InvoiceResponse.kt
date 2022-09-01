package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.InvoiceBody


data class InvoiceResponse(
        val StartDate: String,
        val EndDate: String,
        val TotalOverDues: Int,
        val TotalOverDuesAmount: Float,
        val Invoices: List<InvoiceBody>,
        val Contacts: List<ContactResponse>,
        val Venues: List<VenueResponseBody>
) {


//    val invoiceType: InvoiceType?,
//    val invoiceNumber: String?,
//    val orderNumber: String,
//    val purchaseOrder: String?,
//    val invoiceDate: String?,
//    val amount: Float,
//    val dueDate: String,
//    val paid: Boolean,
//    val overDue: Boolean,
//    val venueId: String?,
//    val contactPersonId: String?
//
//    fun toInvoice() = Invoice(
//            invoiceType = invoiceType,
//            invoiceNumber = invoiceNumber,
//            orderNumber = orderNumber,
//            purchaseOrder = purchaseOrder,
//            invoiceDate = invoiceDate,
//            amount = amount,
//            dueDate = dueDate,
//            paid = paid,
//            overDue = overDue,
//            venueId = venueId,
//            contactPersonId = contactPersonId
//    )
}