package com.riwal.rentalapp.machinedetail.ar

import android.view.View
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_ar_help_item.*

class ArHelpItemViewHolder(override val containerView: View) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    var text
        get() = itemTextView.text
        set(value) {
            itemTextView.text = value
        }

    override fun prepareForDisplay() {
        val itemIndex = indexPath!!.row + 1
        itemIndexTextView.text = "$itemIndex."
    }

}