package com.riwal.rentalapp.myprojects.machinetransfernotifications

import com.riwal.rentalapp.common.extensions.datetime.isoString
import com.riwal.rentalapp.common.extensions.datetime.today
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.AcceptTransferMachineRequestBody
import com.riwal.rentalapp.model.api.RejectTransferMachineRequestBody
import com.riwal.rentalapp.uploadimage.UploadImageController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TransferNotificationController(
        val view: TransferNotificationView,
        val orderManager: OrderManager,
        val activeCountry: Country,
        val sessionManager: SessionManager
) : ViewLifecycleObserver, TransferNotificationView.DataSource, TransferNotificationView.Delegate, UploadImageController.Delegate, CoroutineScope by MainScope() {

    /*--------------------------------------- Properties -----------------------------------------*/


    private var transferNotification: MutableList<MachineTransferNotification> = mutableListOf()
    private var selectedNotification: MachineTransferNotification? = null
    private var isUpdatingTransferNotification = false
    private var hasFailedLoadingNotification = false

    var toCustomer: AcceptTransferMachineRequestBody.acceptCustomersRequestBody? = null

    var transferId: String? = null
        set(value) {
            transferNotification.last { it.transferId == value }.status = MachineTransferNotificationStatus.TRANSFER_ACCEPTED
            view.notifyDataChanged()
        }

    private val user
        get() = sessionManager.user


    /*---------------------------------------- Lifecycle -----------------------------------------*/

    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        getTransferNotification()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*----------------------------------------  DataSource ---------------------------------------*/

    override fun notification(view: TransferNotificationView) = transferNotification.reversed()

    override fun isUpdatingTransferMachine(view: TransferNotificationView) = isUpdatingTransferNotification

    override fun hasFailedLoadingNotification(view: TransferNotificationView) = hasFailedLoadingNotification

    override fun activeCountry(view: TransferNotificationView) = activeCountry

    override fun user(view: TransferNotificationView) = user

    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun imageUploadCompleted(transferId: String) {
        transferNotification.last { it.transferId == transferId }.status = MachineTransferNotificationStatus.TRANSFER_ACCEPTED
        view.notifyDataChanged()
    }

    override fun onRejectTransferSelected(notification: RejectTransferMachineRequestBody) {
        view.showConfirmDialog(notification) {
            launch {
                try {
                    orderManager.submitRejectTransferRequest(notification)
                    transferNotification.removeAt(transferNotification.indexOfLast { it.transferId == notification.transferId })
                    view.notifyDataChanged()
                    postEvent(Notification.SEND_REJECT_TRANSFER_REQUEST_SUCCEEDED)
                } catch (error: Exception) {
                    postEvent(Notification.SEND_REJECT_TRANSFER_REQUEST_FAILED)
                }
            }

        }
    }

    override fun onAcceptTransferSelected(notification: MachineTransferNotification) {
        selectedNotification = notification
        view.navigateToImageUploadPage { destination ->
            val controller = destination as UploadImageController
            controller.delegate = this

            val acceptTransferRequest = AcceptTransferMachineRequestBody(
                    transferDate = today.toString(),
                    userName = user!!.name,
                    transferId = selectedNotification!!.transferId,
                    fromCustomer = AcceptTransferMachineRequestBody.acceptCustomersRequestBody(
                            companyId = selectedNotification!!.fromCustomer.companyId,
                            id = selectedNotification!!.fromCustomer.customerId,
                            name = selectedNotification!!.fromCustomer.name,
                            contact = AcceptTransferMachineRequestBody.acceptContactRequestBody(
                                    name = selectedNotification!!.fromCustomer.contact?.name,
                                    phone = selectedNotification!!.fromCustomer.contact?.phoneNumber,
                                    email = selectedNotification!!.fromCustomer.contact?.email)),
                    toCustomer = toCustomer!!,
                    machine = AcceptTransferMachineRequestBody.acceptMachineRequestBody(
                            rentalType = selectedNotification!!.machine!!.rentalType,
                            brand = selectedNotification!!.machine!!.machineType,
                            fleetNumber = selectedNotification!!.machine!!.fleetNumber,
                            onHireDate = selectedNotification!!.machine!!.onRentDateTime?.isoString,
                            offHireDate = selectedNotification!!.machine!!.offRentDateTime?.isoString,
                            serialNumber = selectedNotification!!.machine!!.serialNumber,
                            orderNumber = selectedNotification!!.machine!!.orderNumber,
                            purchaseOrder = selectedNotification!!.machine!!.purchaseOrder!!),
                    countryCode = activeCountry
            )

            controller.acceptTransferRequestBody = acceptTransferRequest

        }
    }

    override fun onRetryLoadingTransferNotificationSelected(view: TransferNotificationView) {
        getTransferNotification()
    }


    /*------------------------------------- Private methods --------------------------------------*/

    override fun onNotificationStatusUpdated(transferId: String) {
        this.transferId = transferId
    }

    private fun getTransferNotification() = launch {

        isUpdatingTransferNotification = true
        view.notifyDataChanged()

            try {
                transferNotification = orderManager.getTransferMachineNotification(toCustomer!!.id).toMutableList()
                hasFailedLoadingNotification = false
            } catch (error: Exception) {
                transferNotification = mutableListOf()
                hasFailedLoadingNotification = true
            }

        isUpdatingTransferNotification = false
        view.notifyDataChanged()
    }





}