package com.riwal.rentalapp.myrentals

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.RentalStatus

interface MyRentalsView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun showOffRentPanel(preparationHandler: ControllerPreparationHandler)
    fun showCommentsDialog(callback: (comments: String) -> Unit)
    fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun rentals(view: MyRentalsView): List<Rental>
        fun filter(view: MyRentalsView): MyRentalsFilter
        fun canChangePeriod(view: MyRentalsView): Boolean
        fun changeableOrderStatuses(view: MyRentalsView): List<RentalStatus>
        fun resultsFormat(view: MyRentalsView): MyRentalsResultsFormat
        fun activeCountry(view: MyRentalsView): Country
        fun isUpdatingRentals(view: MyRentalsView): Boolean
        fun isFilteringRentals(view: MyRentalsView): Boolean
        fun canStopRenting(view: MyRentalsView, rental: Rental): Boolean
        fun isInSelectionMode(view: MyRentalsView): Boolean
        fun selectedRentals(view: MyRentalsView): List<Rental>
        fun isChatEnabled(view: MyRentalsView): Boolean
        fun numberOfUnreadMessages(view: MyRentalsView): Int
        fun hasFailedLoadingRentals(view: MyRentalsView): Boolean
        fun isPhoneCallEnable(view: MyRentalsView): Boolean
        fun rentalDeskContactInfo(view: MyRentalsView): List<ContactInfo>
    }

    interface Delegate {
        fun onRetryLoadingRentalsSelected(view: MyRentalsView)
        fun onFilterChanged(view: MyRentalsView, newFilter: MyRentalsFilter)
        fun onRentalSelected(view: MyRentalsView, rental: Rental)
        fun onResultsFormatChanged(view: MyRentalsView, newResultsFormat: MyRentalsResultsFormat)
        fun onEnableSelectionModeSelected(view: MyRentalsView)
        fun onCancelSelectionModeSelected(view: MyRentalsView)
        fun onStopRentingSelected(view: MyRentalsView)
        fun onRefreshSelected(view: MyRentalsView)
    }

}

enum class MyRentalsResultsFormat {
    LIST,
    MAP
}
