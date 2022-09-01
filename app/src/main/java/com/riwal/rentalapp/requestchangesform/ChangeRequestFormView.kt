package com.riwal.rentalapp.requestchangesform

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.Rental

interface ChangeRequestFormView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToPlacePickerPage(controllerPreparationHandler: ControllerPreparationHandler)
    fun askForComments(completionHandler: (comments: String) -> Unit)

    interface DataSource {
        fun rental(view: ChangeRequestFormView): Rental?
        fun accessories(view: ChangeRequestFormView): AccessoriesOnRent?
        fun canChangeOnRentDateTime(view: ChangeRequestFormView): Boolean
        fun canChangeOffRentDateTime(view: ChangeRequestFormView): Boolean
        fun canSubmit(view: ChangeRequestFormView): Boolean
        fun firstDayOfWeek(view: ChangeRequestFormView): Int
    }

    interface Delegate {
        fun onBackPressed(view: ChangeRequestFormView)
        fun onPickPlaceSelected(view: ChangeRequestFormView)
        fun onRentalChanged(view: ChangeRequestFormView, changedRental: Rental)
        fun onAccessoriesChanged(view: ChangeRequestFormView, changedAccessories: AccessoriesOnRent)
        fun onSubmitChangesSelected(view: ChangeRequestFormView)
    }

}