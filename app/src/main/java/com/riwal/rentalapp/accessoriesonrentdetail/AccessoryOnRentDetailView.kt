package com.riwal.rentalapp.accessoriesonrentdetail

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.ContactInfo

interface AccessoryOnRentDetailView : MvcView, ObservableLifecycleView {

    var dataSource: Datasource
    var delegate: Delegate


    fun navigateToChangeRequestPage(preparationHandler: ControllerPreparationHandler)
    fun showOffRentPanel(preparationHandler: ControllerPreparationHandler)
    fun askForComments(callback: (comments: String) -> Unit)

    interface Datasource {
        fun accessoriesOnRent(view: AccessoryOnRentDetailView): AccessoriesOnRent
        fun canStopRenting(view: AccessoryOnRentDetailView): Boolean
        fun canCancelRenting(view: AccessoryOnRentDetailView): Boolean
        fun canRequestChanges(view: AccessoryOnRentDetailView): Boolean
        fun isChatEnabled(view: AccessoryOnRentDetailView): Boolean
        fun shouldShowLastInspectionDate(view: AccessoryOnRentDetailView): Boolean
        fun numberOfUnreadMessages(view: AccessoryOnRentDetailView): Int
        fun isPhoneCallEnable(view: AccessoryOnRentDetailView): Boolean
        fun rentalDeskContactInfo(view: AccessoryOnRentDetailView): List<ContactInfo>
    }

    interface Delegate {
        fun onStopRentingSelected()
        fun onCancelRentingSelected()
        fun onRequestChangesSelected()
    }
}