package com.riwal.rentalapp.scanmachineqrcode

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface ScanMachineQRCodeView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun notifyErrorOccurred(error: ScanMachineQRCodeError)
    fun navigateToRentalDetailPage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun isLoadingRental(view: ScanMachineQRCodeView): Boolean
    }

    interface Delegate {
        fun cameraUsageDeniedOrFailed(view: ScanMachineQRCodeView)
        fun QRCodeScanned(view: ScanMachineQRCodeView, contents: String)
        fun navigateBackSelected(view: ScanMachineQRCodeView)
    }
}