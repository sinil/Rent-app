package com.riwal.rentalapp.common.ui

import android.view.View
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import kotlinx.android.synthetic.main.row_single_line_key_value.view.*

class SingleLineKeyValueRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {


    var keyValue: Pair<String, String?>
        get() = (key to value)
        set(keyValue) {
            key = keyValue.component1()
            value = keyValue.component2()
        }

    var key: String
        get() = itemView.keyTextView.text.toString()
        set(value) {
            itemView.keyTextView.text = value
        }

    var value: String?
        get() = itemView.valueTextView.text.toString()
        set(value) {
            itemView.valueTextView.text = value
        }

}