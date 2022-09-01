package com.riwal.rentalapp.uploadimage

import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.OrderManager
import com.riwal.rentalapp.model.api.AcceptTransferMachineRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class UploadImageController(val view: UploadImageView, val orderManager: OrderManager, val activeCountry: Country) : ViewLifecycleObserver, UploadImageView.DataSource, UploadImageView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    var acceptTransferRequestBody: AcceptTransferMachineRequestBody? = null
    var selectedImageFiles: Array<String>? = null
    private var isUploadingImage = false
    private var canUploadFile = false
    var delegate: Delegate? = null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.delegate = this
        view.dataSource = this

        observe(view)
    }


    /*---------------------------------- UploadImageView DataSource ----------------------------------*/


    override fun isUploadingImages(view: UploadImageView) = isUploadingImage

    override fun canUploadFile(view: UploadImageView) = canUploadFile


    /*----------------------------------- UploadImageView Delegate -----------------------------------*/


    override fun selectedImages(selectedImages: Array<String>?) {
        selectedImageFiles = selectedImages

    }

    override fun canUploadButtonClick(isClick: Boolean) {
        canUploadFile = isClick
        view.notifyDataChanged()
    }

    override fun onSelectUploadImage(view: UploadImageView) {
        view.showConformationDialog({
            isUploadingImage = true
            view.notifyDataChanged()
        }, {
            canUploadFile = true
            isUploadingImage = false
            view.notifyDataChanged()
        })
    }

    override fun uploadImage() {
        acceptTransferWitUploadImage()
    }


    override fun onBackButtonClicked() {
        view.navigateBack()
    }


    /*----------------------------------- private methods -----------------------------------*/


    private fun acceptTransferWitUploadImage() = launch {

        try {
            orderManager.submitAcceptTransferRequest(activeCountry.company.toString(), acceptTransferRequestBody!!, selectedImageFiles)
            postEvent(Notification.SEND_ACCEPT_TRANSFER_REQUEST_SUCCEEDED)
            delegate!!.onNotificationStatusUpdated( acceptTransferRequestBody!!.transferId)
            view.navigateBack()

        } catch (error: Exception) {
            postEvent(Notification.SEND_ACCEPT_TRANSFER_REQUEST_FAILED)
            canUploadFile = true
        }
        isUploadingImage = false
        view.notifyDataChanged()

    }


    /*------------------------------------- Interfaces --------------------------------------*/

    interface Delegate {
        fun onNotificationStatusUpdated(transferId: String)
    }

}