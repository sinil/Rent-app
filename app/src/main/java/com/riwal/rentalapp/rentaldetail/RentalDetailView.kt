package com.riwal.rentalapp.rentaldetail


import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.RentalDetail

interface RentalDetailView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToMachineDetailsPage(prepareHandler: ControllerPreparationHandler)
    fun navigateToChangeRequestPage(preparationHandler: ControllerPreparationHandler)
    fun showOffRentPanel(preparationHandler: ControllerPreparationHandler)
    fun askForComments(callback: (comments: String) -> Unit)
    fun navigateToReportBreakdownPage(preparationHandler: ControllerPreparationHandler)

    interface DataSource {
        fun rental(view: RentalDetailView): Rental
        fun rentalDetail(view: RentalDetailView): RentalDetail?
        fun rentalDeskContacts(view: RentalDetailView): List<ContactInfo>
        fun canStopRenting(view: RentalDetailView): Boolean
        fun canCancelRenting(view: RentalDetailView): Boolean
        fun canRequestChanges(view: RentalDetailView): Boolean
        fun isLoadingMachine(view: RentalDetailView): Boolean
        fun isLoadingRequestedMachine(view: RentalDetailView): Boolean
        fun isViewPresentedModally(view: RentalDetailView): Boolean
        fun isChatEnabled(view: RentalDetailView): Boolean
        fun shouldShowLastInspectionDate(view: RentalDetailView): Boolean
        fun numberOfUnreadMessages(view: RentalDetailView): Int
        fun canSendBreakdownReport(view: RentalDetailView): Boolean
        fun isReceivedMachineDifferent(view: RentalDetailView): Boolean
        fun isPhoneCallEnable(view: RentalDetailView): Boolean
        fun rentalDeskContactInfo(view: RentalDetailView): List<ContactInfo>
    }

    interface Delegate {
        fun onMachineSelected(isReceivedMachine: Boolean)
        fun onStopRentingSelected()
        fun onCancelRentingSelected()
        fun onRequestChangesSelected()
        fun onReportBreakdownSelected()
    }

}