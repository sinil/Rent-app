package com.riwal.rentalapp.main.cart

import android.view.View
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.AccessoryOrder
import com.riwal.rentalapp.model.MachineOrder
import kotlinx.android.synthetic.main.row_accessory_order.view.*

class AccessoryOrderRowViewHolder(itemView: View, val machineOrder: MachineOrder?, val delegate: CartView.Delegate?) : EasyRecyclerView.ViewHolder(itemView) {


    fun updateWith(accessoryOrder: AccessoryOrder) {

        itemView.accessoryName.text = accessoryOrder.accessory.name
        itemView.accessoryQuantity.text = accessoryOrder.quantity.toString()

        itemView.deleteAccessory.setOnClickListener {
            delegate?.onAccessoryDelete(machineOrder, accessoryOrder)
        }
    }
}