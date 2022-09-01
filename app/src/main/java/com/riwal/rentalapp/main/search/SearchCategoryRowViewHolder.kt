package com.riwal.rentalapp.main.search

import android.view.View
import androidx.annotation.DrawableRes
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.getString
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import kotlinx.android.synthetic.main.row_machine_type.view.*

class SearchCategoryRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    var title
        get() = itemView.categoryTextView.text
        set(value) {
            itemView.categoryTextView.text = value
        }

    fun setImageResource(@DrawableRes resourceId: Int) {
        itemView.categoryImageView.setImageResource(resourceId)
    }

    fun updateWith(category: Category) {
        when (category) {
//            is HelpMeChooseCategory -> {
//                title = itemView.getString(R.string.help_me_choose_wizard_title)
//                setImageResource(R.drawable.img_machines_contour)
//                itemView.categoryImageView.setTintFromResource(R.color.material_icon_disabled)
//            }
            is MachineCategory -> {
                title = itemView.getString(category.machineType.localizedNameRes)
                setImageResource(category.machineType.imageRes)
                itemView.categoryImageView.imageTintList = null
            }
        }
    }

}
