package com.riwal.rentalapp.common.ui.easyrecyclerview

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_section.view.*

class SectionRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    var title: String? = null
        set(value) {
            itemView.sectionTitle.text = value
            // For some reason when the first row has a height of 0, it is not possible to pull
            // down the SwipeRefreshLayout. Therefore we give it a height of 1 -_-
            // TODO: We need to figure out why this happens and find a proper solution
            val params = itemView.layoutParams
            params.height = if (value == null) 1 else ViewGroup.LayoutParams.WRAP_CONTENT
            itemView.layoutParams = params
        }
}
