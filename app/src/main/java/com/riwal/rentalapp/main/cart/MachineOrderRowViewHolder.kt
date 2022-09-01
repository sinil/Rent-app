package com.riwal.rentalapp.main.cart

import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.AccessoryOrder
import com.riwal.rentalapp.model.MachineOrder
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.EXACT
import kotlinx.android.synthetic.main.row_machine_order.view.*

class MachineOrderRowViewHolder(itemView: View, val delegate: CartView.Delegate?)
    : EasyRecyclerView.ViewHolder(itemView), EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {

    var machineOrder: MachineOrder? = null
    private lateinit var accessoriesOrder: List<AccessoryOrder>

    fun updateWith(machineOrder: MachineOrder, isAddAccessoriesEnable: Boolean) {

        itemView.addAccessoriesButton.isVisible = isAddAccessoriesEnable

        this.machineOrder = machineOrder

        this.accessoriesOrder = machineOrder.accessories.filter { it.quantity > 0 }

        val (machine, quantity, onRentDate, deliveryTime, offRentDate, offRentTime, offRentDateStrictness) = machineOrder

        itemView.iconImageView.loadImage(machine.thumbnailUrl, placeholder = R.drawable.img_machines_contour)

        itemView.titleTextView.text = machine.rentalType
        itemView.fromTextView.text = onRentDate.toDateTime(deliveryTime).toMediumStyleString(context)
        itemView.toTextView.text = if (offRentDateStrictness == EXACT) offRentDate.toDateTime(offRentTime!!).toMediumStyleString(context) else getString(R.string.open_rental)
        itemView.quantityTextView.text = quantity.toString()

        itemView.accessoriesRecyclerView.dataSource = this
        itemView.accessoriesRecyclerView.delegate = this

        itemView.accessoriesRecyclerView.notifyDataSetChanged()
    }

    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int = accessoriesOrder.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int = R.layout.row_accessory_order
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder = AccessoryOrderRowViewHolder(itemView, machineOrder, delegate)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        viewHolder as AccessoryOrderRowViewHolder
        val accessory = accessoriesOrder[indexPath.row]
        viewHolder.updateWith(accessory)

    }
}
