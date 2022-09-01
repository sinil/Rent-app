package com.riwal.rentalapp.summary

import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.MachineOrder
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.EXACT
import kotlinx.android.synthetic.main.row_machine_summary.view.*

class MachineSummaryRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    fun updateWith(machineOrder: MachineOrder) {

        val (machine, quantity, onRentDate, deliveryTime, offRentDate, offRentTime, offRentDateStrictness) = machineOrder

        itemView.titleTextView.text = "$quantity × ${machine.rentalType}"
        itemView.fromTextView.text = onRentDate.toDateTime(deliveryTime).toMediumStyleString(context)
        itemView.toTextView.text = if (offRentDateStrictness == EXACT) offRentDate.toDateTime(offRentTime!!).toMediumStyleString(context) else getString(R.string.open_rental)
    }

}
