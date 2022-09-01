package com.riwal.rentalapp.scanmachineqrcode

import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.RentalManager
import com.riwal.rentalapp.rentaldetail.RentalDetailController
import com.riwal.rentalapp.scanmachineqrcode.ScanMachineQRCodeError.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ScanMachineQRCodeController(
        val view: ScanMachineQRCodeView,
        val currentCustomer: Customer,
        val rentalManager: RentalManager
) : ViewLifecycleObserver, ScanMachineQRCodeView.DataSource, ScanMachineQRCodeView.Delegate, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var isLoadingRental = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private var isProcessingQRCode = false


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        view.dataSource = this
        view.delegate = this

        observe(view)
    }


    /*--------------------------------- Scan Machine QR Code View Data Source ---------------------------------*/


    override fun isLoadingRental(view: ScanMachineQRCodeView): Boolean = isLoadingRental


    /*--------------------------------- Scan Machine QR Code View Delegate ---------------------------------*/

    override fun cameraUsageDeniedOrFailed(view: ScanMachineQRCodeView) = view.navigateBack()

    override fun QRCodeScanned(view: ScanMachineQRCodeView, contents: String) = processQRCode(contents)

    override fun navigateBackSelected(view: ScanMachineQRCodeView) = view.navigateBack()

    /*----------------------------------------- Private Methods ------------------------------------------*/


    private fun navigateToRentalDetailPage(rental: Rental) {
        view.navigateToRentalDetailPage { destination ->
            val controller = destination as RentalDetailController
            controller.rental = rental
        }
    }

    private fun processQRCode(contents: String) {

        if (!isProcessingQRCode)
            isProcessingQRCode = true


        MachineQRCodeParser.fleetNumber(contents = contents)?.let {
            isLoadingRental = true
            launch {
                rentalManager.getCurrentRental(fleetNumber = it, customer = currentCustomer)?.let {
                    onQRCodeProcessingCompleted(currentRental = it, error = null)
                } ?: onQRCodeProcessingCompleted(currentRental = null, error = RentalNotFound)
            }

        } ?: onQRCodeProcessingCompleted(currentRental = null, error = InvalidQRCode)

    }

    private fun onQRCodeProcessingCompleted(currentRental: Rental?, error: ScanMachineQRCodeError?) {

        if (currentRental != null) {
            navigateToRentalDetailPage(currentRental)
        } else {
            view.notifyErrorOccurred(error = error!!)
        }

        isLoadingRental = false
        isProcessingQRCode = false
    }
}