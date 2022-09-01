package com.riwal.rentalapp.accessoriesonrent

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.RentalStatus
import com.riwal.rentalapp.myrentals.MyRentalsView

interface AccessoriesOnRentView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun showOffRentPanel(preparationHandler: ControllerPreparationHandler)
    fun showCommentsDialog(callback: (comments: String) -> Unit)
    fun navigateToAccessoriesDetailsPage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun accessoriesOnRent(view: AccessoriesOnRentView): List<AccessoriesOnRent>
        fun filter(view: AccessoriesOnRentView): AccessoriesOnRentFilter
        fun canChangePeriod(view: AccessoriesOnRentView): Boolean
        fun changeableOrderStatuses(view: AccessoriesOnRentView): List<RentalStatus>
        fun isChatEnable(view: AccessoriesOnRentView): Boolean
        fun isPhoneCallEnable(view: AccessoriesOnRentView): Boolean
        fun activeCountry(view: AccessoriesOnRentView): Country
        fun canStopRenting(view: AccessoriesOnRentView, accessories: AccessoriesOnRent): Boolean
        fun numberOfUnreadMessages(view: AccessoriesOnRentView): Int
        fun hasFailedLoadingAccessories(view: AccessoriesOnRentView): Boolean
        fun rentalDeskContactInfo(view: AccessoriesOnRentView): List<ContactInfo>
        fun isUpdatingAccessories(view: AccessoriesOnRentView): Boolean
        fun selectedAccessories(view: AccessoriesOnRentView): List<AccessoriesOnRent>
        fun isInSelectionMode(view: AccessoriesOnRentView): Boolean
    }

    interface Delegate {
        fun onAccessoriesSelected(view: AccessoriesOnRentView, accessoriesOnRent: AccessoriesOnRent)
        fun onRetryLoadingAccessoriesSelected(view: AccessoriesOnRentView)
        fun onEnableSelectionModeSelected(view: AccessoriesOnRentView)
        fun onCancelSelectionModeSelected(view: AccessoriesOnRentView)
        fun onStopRentingSelected(view: AccessoriesOnRentView)
        fun onFilterChanged(view: AccessoriesOnRentView, newFilter: AccessoriesOnRentFilter)
    }

}