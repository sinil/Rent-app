package com.riwal.rentalapp.myquotations

import com.riwal.rentalapp.common.extensions.core.contains
import com.riwal.rentalapp.common.extensions.core.replaceElement
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_FUTURE
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST
import com.riwal.rentalapp.model.Quotation
import org.joda.time.LocalDate

data class MyQuotationsFilter(
        val query: String = "",
        val period: ClosedRange<LocalDate> = DISTANT_PAST.toLocalDate()..DISTANT_FUTURE.toLocalDate(),
        val selectedPaid: List<Boolean> = emptyList(),
        var relevantVenues: List<String?> = emptyList(),
        val selectedVenue: List<String> = emptyList(), // code
        var relevantContacts: List<String?> = emptyList(),
        val selectedContacts: List<String> = emptyList() // id
) {

    private val searchTerms: List<String>
        get() = query.replace(",", " ").split(" ").filter { it.isNotEmpty() }


    fun applyTo(input: List<Quotation>): List<Quotation> {
        var quotation: List<Quotation> = input
                .filter { it.searchableProperties().contains(searchTerms, ignoreCase = true) }
                .filter { quotation ->
                    val relevantSelectedVenue = selectedVenue.filter { it in relevantVenues }
                    relevantSelectedVenue.isEmpty() || quotation.venueCity in relevantSelectedVenue
                }
                .filter { quotation ->
                    val relevantSelectedContact = selectedContacts.filter { it in relevantContacts }
                    relevantSelectedContact.isEmpty() || quotation.contactPerson in relevantSelectedContact
                }

        return quotation

    }

}

private fun Quotation.searchableProperties() = "$quotationNo $customerOrderRefNo $venueCity $contactPerson $customerId}"