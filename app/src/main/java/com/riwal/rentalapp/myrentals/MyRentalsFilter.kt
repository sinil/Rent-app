package com.riwal.rentalapp.myrentals

import android.content.res.Resources
import com.riwal.rentalapp.common.extensions.core.contains
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_FUTURE
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST
import com.riwal.rentalapp.common.extensions.core.overlaps
import com.riwal.rentalapp.model.RentalStatus
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.searchableProperties
import org.joda.time.LocalDate

data class MyRentalsFilter(
        val query: String = "",
        val period: ClosedRange<LocalDate> = DISTANT_PAST.toLocalDate()..DISTANT_FUTURE.toLocalDate(),
        val selectedRentalStatuses: List<RentalStatus> = emptyList(),
        val relevantRentalStatuses: List<RentalStatus> = RentalStatus.allExceptUnknown
) {

    val searchTerms: List<String>
        get() = query.replace(",", " ").split(" ").filter { it.isNotEmpty() }

    fun applyTo(input: List<Rental>, resources: Resources) = input
            .filter { it.searchableProperties(resources).contains(searchTerms, ignoreCase = true) }
            .filter { rental ->
                val onRentDate = rental.onRentDateTime.toLocalDate()
                val hasFinalOffRentDateTime = rental.offRentDateTime != null && rental.isOffRentDateFinal
                val finalOffRentDate = if (hasFinalOffRentDateTime) rental.offRentDateTime!!.toLocalDate() else DISTANT_FUTURE.toLocalDate()
                val rentalPeriod = onRentDate..finalOffRentDate
                rentalPeriod overlaps period
            }
            .filter { rental -> rental.status in relevantRentalStatuses }
            .filter { rental ->
                val relevantSelectedOrderStatuses = selectedRentalStatuses.filter { it in relevantRentalStatuses }
                relevantSelectedOrderStatuses.isEmpty() || rental.status in relevantSelectedOrderStatuses
            }
}

private fun Rental.searchableProperties(resources: Resources) = "$rentalType $fleetNumber $machineType $orderNumber $purchaseOrder ${contact.name} $orderedById $orderedByName ${project.name} ${project.address} ${project.contactName} ${machine?.searchableProperties(resources)}"
