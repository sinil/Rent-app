package com.riwal.rentalapp.project

import android.view.View
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Project
import kotlinx.android.synthetic.main.row_project.view.*

class ProjectRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    val radioButton
        get() = itemView.radioButton

    val editButton
        get() = itemView.editButton

    var isChecked
        get() = radioButton.isChecked
        set(value) {
            radioButton.isChecked = value
        }

    fun updateWith(project: Project) {
        itemView.projectNameTextView.text = project.name
        itemView.contactNameTextView.text = project.contactName
        itemView.locationTextView.text = project.address
    }

}
