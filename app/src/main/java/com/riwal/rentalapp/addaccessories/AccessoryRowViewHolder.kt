package com.riwal.rentalapp.addaccessories

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import kotlinx.android.synthetic.main.row_add_accessory.view.*
import android.widget.ArrayAdapter
import com.riwal.rentalapp.R
import com.riwal.rentalapp.model.AccessoryOrder


class AccessoryRowViewHolder(itemView: View, val context: Context, val delegate: AddAccessoriesView.Delegate) : EasyRecyclerView.ViewHolder(itemView) {

    private var spinnerTouched: Boolean = false

    private val accessoryTextView
        get() = itemView.accessoryTextView!!

    private val accessoryImageView
        get() = itemView.accessoryImageView!!

    private val quantitySpinner
        get() = itemView.quantitySpinner!!


    @SuppressLint("ClickableViewAccessibility")
    fun updateWith(accessory: AccessoryOrder) {

        accessoryTextView.text = accessory.accessory.name
        accessoryImageView.loadImage(accessory.accessory.imageUrl, placeholder = R.drawable.img_machines_contour)


        if (accessory.accessory.quantities != null) {
            val quantitiesAdapter = ArrayAdapter<Int>(context, android.R.layout.simple_dropdown_item_1line, accessory.accessory.quantities!!)

            quantitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            quantitySpinner.setAdapter(quantitiesAdapter)
            quantitySpinner.setOnTouchListener { _, _ ->
                spinnerTouched = true
                return@setOnTouchListener false
            }

            quantitySpinner.selectedIndex = accessory.accessory.quantities!!.indexOf(accessory.quantity)

            quantitySpinner.setOnItemSelectedListener { view, position, id, item -> delegate.onAccessoryQuantitySelected(accessory.accessory, accessory.accessory.quantities!![position]) }
        }

    }

}