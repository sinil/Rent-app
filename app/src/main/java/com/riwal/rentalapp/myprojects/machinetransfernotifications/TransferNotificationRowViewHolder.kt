package com.riwal.rentalapp.myprojects.machinetransfernotifications

import android.content.Context
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.MachineTransferNotificationStatus.*
import com.riwal.rentalapp.model.api.RejectTransferMachineRequestBody
import kotlinx.android.synthetic.main.row_transfer_notification.view.*


class TransferNotificationRowViewHolder(itemView: View, val context: Context, val delegate: TransferNotificationView.Delegate) : EasyRecyclerView.ViewHolder(itemView) {


    private val notificationDescriptionTextView
        get() = itemView.notificationDescriptionTextView

    private val acceptButton
        get() = itemView.acceptButton

    private val rejectButton
        get() = itemView.rejectButton

    private val buttonContainer
        get() = itemView.buttonContainer

    private val notificationContainer
        get() = itemView.transferMachineNotificationContainer


    fun updateWith(notification: MachineTransferNotification, activeCountry: Country, currentCustomer: Customer?) {


        acceptButton.setOnClickListener {
            delegate.onAcceptTransferSelected(notification)
            acceptButton.text = getString(R.string.upload_image)
        }


        rejectButton.setOnClickListener {

            val transferNotificationRequestBody = RejectTransferMachineRequestBody(
                    toCustomer = RejectTransferMachineRequestBody.CustomerRejectTransferBody(
                            id = notification.toCustomer.customerId,
                            name = notification.toCustomer.name,
                            contact = Contact(id = notification.toCustomer.contact?.id,
                                    name = notification.toCustomer.contact!!.name,
                                    phoneNumber = notification.toCustomer.contact.phoneNumber,
                                    email = notification.toCustomer.contact.email)),
                    fromCustomer = RejectTransferMachineRequestBody.CustomerRejectTransferBody(
                            id = notification.fromCustomer.customerId,
                            name = notification.fromCustomer.name,
                            contact = Contact(id = notification.fromCustomer.contact?.id,
                                    name = notification.fromCustomer.contact?.name,
                                    phoneNumber = notification.fromCustomer.contact?.phoneNumber,
                                    email = notification.fromCustomer.contact?.email,
                                    company = notification.fromCustomer.contact?.company)),
                    machine = RejectTransferMachineRequestBody.MachineRejectTransferBody(
                            rentalType = notification.machine!!.rentalType!!,
                            fleetNumber = notification.machine.fleetNumber,
                            onHireDate = notification.machine.onRentDateTime?.toDate().toString(),
                            offHireDate = notification.machine.offRentDateTime?.toDate().toString()),
                    transferId = notification.transferId,
                    countryCode = activeCountry)
            
            delegate.onRejectTransferSelected(transferNotificationRequestBody)
        }



        when (notification.status) {
            TRANSFER_REQUESTED -> {
                buttonContainer.isVisible = notification.status != TRANSFER_REQUESTED
                notificationDescriptionTextView.text = getString(R.string.notification_transfer_requested_message,
                        notification.machine?.rentalType ?: notification.machine!!.machineType,
                        notification.toCustomer.name, notification.toCustomer.contact?.name!!)
            }
            TRANSFER_RECEIVED -> {
                buttonContainer.isVisible = notification.status == TRANSFER_RECEIVED
                notificationDescriptionTextView.text = getString(R.string.notification_transfer_received_message,
                        notification.fromCustomer.contact?.name ?: "", notification.fromCustomer.name, notification.machine?.rentalType ?: notification.machine!!.machineType)
            }
            TRANSFER_ACCEPTED -> {
                buttonContainer.isVisible = notification.status != TRANSFER_ACCEPTED
                if (notification.toCustomer.customerId == currentCustomer?.id){
                    notificationDescriptionTextView.text = getString(R.string.notification_transfer_accepted_acknowledgement_message, notification.machine?.rentalType ?: notification.machine!!.machineType,
                            notification.fromCustomer.contact?.name ?: "", notification.fromCustomer.name)
                }else {
                    notificationDescriptionTextView.text = getString(R.string.notification_transfer_accepted_message, notification.machine?.rentalType ?: notification.machine!!.machineType,
                            notification.toCustomer.contact?.name ?: "", notification.toCustomer.name)
                }

            }
            UNKNOWN -> {
                notificationContainer.isVisible = false
            }
            else -> {
                notificationContainer.isVisible = false
            }
        }

    }


}