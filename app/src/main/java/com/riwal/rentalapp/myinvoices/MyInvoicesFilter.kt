package com.riwal.rentalapp.myinvoices

import com.riwal.rentalapp.common.extensions.core.contains
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_FUTURE
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST
import com.riwal.rentalapp.model.Invoice
import com.riwal.rentalapp.model.InvoiceTypes
import org.joda.time.LocalDate

data class MyInvoicesFilter(
        val query: String = "",
        val period: ClosedRange<LocalDate> = DISTANT_PAST.toLocalDate()..DISTANT_FUTURE.toLocalDate(),
        val selectedInvoiceType: List<InvoiceTypes> = emptyList(),
        val selectedPaid: List<Boolean> = emptyList(),
        val selectedOverdue: List<Boolean> = emptyList(),
        var relevantVenues: List<String?> = emptyList(),
        val selectedVenue: List<String> = emptyList(), // code
        var relevantContacts: List<String?> = emptyList(),
        val selectedContacts: List<String> = emptyList() // id
) {

    private val relevantInvoiceType: List<InvoiceTypes> = InvoiceTypes.allExceptOther

    private val searchTerms: List<String>
        get() = query.replace(",", " ").split(" ").filter { it.isNotEmpty() }

    fun applyTo(input: List<Invoice>): List<Invoice> {
        var invoices: List<Invoice> = input
                .filter { it.searchableProperties().contains(searchTerms, ignoreCase = true) }
                .filter { invoice ->
                    val relevantSelectedInvoiceType = selectedInvoiceType.filter { it in relevantInvoiceType }
                    relevantSelectedInvoiceType.isEmpty() || invoice.invoiceType in relevantSelectedInvoiceType
                }
                .filter { invoice ->
                    val relevantSelectedVenue = selectedVenue.filter { it in relevantVenues }
                    relevantSelectedVenue.isEmpty() || invoice.venueCode in relevantSelectedVenue
                }
                .filter { invoice ->
                    val relevantSelectedContact = selectedContacts.filter { it in relevantContacts }
                    relevantSelectedContact.isEmpty() || invoice.contactPersonId in relevantSelectedContact
                }

        if (selectedPaid.isNotEmpty()) {
            invoices = invoices.filter { invoice -> invoice.paid in selectedPaid }
        }

        // The business logic is that the overdue filter is relevant to invoices that are not paid yet
        // whenever you check the overdue filter yes or no , implicitly you should be filtering invoices based on x.IsPaid == false as well
        if (selectedOverdue.isNotEmpty()) {
            invoices = invoices.filter { invoice -> !invoice.paid }
            invoices = if (selectedOverdue.size == 2) {
                invoices.filter { invoice -> invoice.overDue <= 0 || invoice.overDue > 0 }
            } else {
                if (selectedOverdue.contains(true)) {
                    invoices.filter { invoice -> invoice.overDue > 0 }
                } else {
                    invoices.filter { invoice -> invoice.overDue <= 0 }
                }
            }

        }
        return invoices

    }
}

private fun Invoice.searchableProperties() = "$invoiceType $invoiceNumber $orderNumber $purchaseOrder $purchaseOrder}"
