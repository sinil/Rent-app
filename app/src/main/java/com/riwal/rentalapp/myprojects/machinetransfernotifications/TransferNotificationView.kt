package com.riwal.rentalapp.myprojects.machinetransfernotifications

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.MachineTransferNotification
import com.riwal.rentalapp.model.User
import com.riwal.rentalapp.model.api.RejectTransferMachineRequestBody

interface TransferNotificationView : MvcView, ObservableLifecycleView {

    var delegate: Delegate
    var dataSource: DataSource

    fun navigateBack()
    fun navigateToImageUploadPage(preparationHandler: ControllerPreparationHandler)
    fun showConfirmDialog(transferMachine: RejectTransferMachineRequestBody, callback: () -> Unit)

    interface DataSource {
        fun notification(view: TransferNotificationView): List<MachineTransferNotification>
        fun isUpdatingTransferMachine(view: TransferNotificationView): Boolean
        fun hasFailedLoadingNotification(view: TransferNotificationView): Boolean
        fun activeCountry(view: TransferNotificationView): Country
        fun user(view: TransferNotificationView): User?
    }

    interface Delegate {

        fun onRejectTransferSelected(notification: RejectTransferMachineRequestBody)
        fun onAcceptTransferSelected(notification: MachineTransferNotification)
        fun onRetryLoadingTransferNotificationSelected(view: TransferNotificationView)
        fun imageUploadCompleted(transferId: String)
    }

}