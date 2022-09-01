package com.riwal.rentalapp.myprojects

import android.content.Context
import android.util.Log
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.MachineBody
import com.riwal.rentalapp.model.MachineTransferStatus.*
import kotlinx.android.synthetic.main.row_my_project.view.*

class MyProjectRowViewHolder(itemView: View, val context: Context, val delegate: MyProjectsView.Delegate) : EasyRecyclerView.ViewHolder(itemView) {


    private val machineImage
        get() = itemView.machineImageView

    private val machineTypeTextView
        get() = itemView.machineTypeTextView

    private val fleetNumberTextView
        get() = itemView.fleetNumberTextView

    private val startRentTextView
        get() = itemView.startRentDateTextView

    private val stopRentDateTextView
        get() = itemView.stopRentDateTextView

    private val transferMachineButton
        get() = itemView.transferMachineButton


    fun updateWith(machines: MachineBody) {

        machineImage.loadImage(machines.image, placeholder = R.drawable.img_machines_contour)

        Log.v("myProject::", "$machines")

        machineTypeTextView.text = machines.rentalType
        fleetNumberTextView.text = machines.fleetNumber
        startRentTextView.text = machines.onRentDateTime?.toLocalDate()?.toMediumStyleString(context)
        stopRentDateTextView.text = machines.offRentDateTime?.toLocalDate()?.toMediumStyleString(context) ?: getString(R.string.open_rental)

        when (machines.status) {
            TRANSFER_MACHINE -> {
                transferMachineButton.isEnabled = machines.status.isTransferable
                transferMachineButton.setText(R.string.transfer_machine)
            }
            PENDING_TRANSFER_MACHINE -> {
                transferMachineButton.isEnabled = !machines.status.isPending
                transferMachineButton.setText(R.string.pending_transfer)
            }
            REQUEST_TRANSFER_MACHINE -> {
                transferMachineButton.isEnabled = !machines.status.isRequestable
                transferMachineButton.setText(R.string.request_transfer)
            }
        }

        transferMachineButton.setOnClickListener { delegate.onMachineTransferSelected(machines) /*Pass My project model class as parameter */ }


    }

}