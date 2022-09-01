package com.riwal.rentalapp.scanmachineqrcode

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.rentaldetail.RentalDetailViewImpl
import kotlinx.android.synthetic.main.custom_barcode_layout.*
import kotlinx.android.synthetic.main.view_scan_qr_code.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import android.content.Intent
import android.net.Uri
import android.provider.Settings


class ScanMachineQRCodeViewImpl : RentalAppNotificationActivity(), ScanMachineQRCodeView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: ScanMachineQRCodeView.DataSource
    override lateinit var delegate: ScanMachineQRCodeView.Delegate


    private val isLoadingRental
        get() = dataSource.isLoadingRental(view = this)

    lateinit var captureManager: CaptureManager

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_scan_qr_code)

        closeButton.setOnClickListener { delegate.navigateBackSelected(view = this) }

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        getCameraPermission()
    }

    override fun updateUI(animated: Boolean) {
        if (isLoadingRental) {
            stopScanning()
        }

        toggleHintProgressView(isLoadingRental)

    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    override fun notifyErrorOccurred(error: ScanMachineQRCodeError) {
        captureManager.onPause()
        var errorMessage = ""
        when (error.name) {
            ScanMachineQRCodeError.InvalidQRCode.name -> errorMessage = getString(R.string.scan_machine_qr_error_invalid_qr_code_message)
            ScanMachineQRCodeError.RentalNotFound.name -> errorMessage = getString(R.string.scan_machine_qr_error_rental_not_found_message)
        }
        AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setPositiveButton(getString(R.string.button_ok)) { dialog, _ ->
                    dialog.dismiss()
                    captureManager.onResume()
                }
                .show()

    }

    override fun navigateToRentalDetailPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RentalDetailViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateBack() = runOnUiThread {
        finish()
    }

    /*------------------------------------- Private methods --------------------------------------*/

    private fun setUpScanner() {

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    toggleHintProgressView(true)
                    delegate.QRCodeScanned(view = this@ScanMachineQRCodeViewImpl, contents = it.text)
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })
    }

    private fun stopScanning() {
        captureManager.onPause()
    }

    private fun startScanning() {
        captureManager.onResume()
    }

    private fun toggleHintProgressView(isLoading: Boolean) {
        if (isLoading) {
            scanQRHint.visibility = View.GONE
            progressLayout.visibility = View.VISIBLE
        } else {
            scanQRHint.visibility = View.VISIBLE
            progressLayout.visibility = View.GONE
        }
    }


    /*------------------------------------- Permission methods --------------------------------------*/

    private fun getCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        setUpScanner()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        displayEnableLocationOnDenied()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        Snackbar.make(barcodeView, getString(R.string.scan_machine_camera_permission_message),
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                }).check()
    }

    private fun displayEnableLocationOnDenied() {
        Snackbar.make(barcodeView, getString(R.string.scan_machine_camera_permission_message),
                Snackbar.LENGTH_LONG).setAction(getString(R.string.settings)) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.show()
    }


}