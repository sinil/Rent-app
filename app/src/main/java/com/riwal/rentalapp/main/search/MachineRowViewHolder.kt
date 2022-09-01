package com.riwal.rentalapp.main.search

import android.annotation.SuppressLint
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.arcore.isArSupported
import com.riwal.rentalapp.common.extensions.core.format
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.localizedMachineType
import kotlinx.android.synthetic.main.row_machine.view.*

class MachineRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    val iconImageView
        get() = itemView.iconImageView!!

    val machineTextView
        get() = itemView.titleTextView!!

    val machineTypeTextView
        get() = itemView.machineTypeTextView!!

    val workingHeightTextView
        get() = itemView.minimumWorkingHeightTextView!!

    val workingOutreachTextView
        get() = itemView.minimumWorkingOutreachTextView!!

    val liftCapacityTextView
        get() = itemView.liftCapacityTextView!!

    val electricPropulsionImageView
        get() = itemView.electricPropulsionImageView!!

    val nonElectricPropulsionImageView
        get() = itemView.nonElectricPropulsionImageView!!

    val arIconImageView
        get() = itemView.arIconImageView

    val modelLayout
        get() = itemView.linearLayoutModel

    val modelTextView
        get() = itemView.modelTextView

    var showModelField = false


    @SuppressLint("SetTextI18n")
    fun updateWith(machine: Machine) {

        val machineType = machine.localizedMachineType(context)
        //                .withCapitalization(resources.configuration.locale, setOf(MID_SENTENCE, NOUN))
        machineTextView.text = machine.rentalType
        machineTypeTextView.text = machineType

        modelLayout.isVisible = showModelField
        if (showModelField) modelTextView.text = machine.brand + " "+machine.model


        workingHeightTextView.text = getString(R.string.value_in_m, machine.workingHeight.format(maximumFractionDigits = 1))
        workingOutreachTextView.text = getString(R.string.value_in_m, machine.workingOutreach.format(maximumFractionDigits = 1))
        liftCapacityTextView.text = getString(R.string.value_in_kg, machine.liftCapacity.toInt().format())
        arIconImageView.isVisible = machine.meshes.isNotEmpty() && isArSupported(itemView.context)

        val hasWorkingOutreach = (machine.workingOutreach > 0f)
        itemView.workingOutreachSection.isVisible = hasWorkingOutreach

        workingOutreachTextView.isVisible = hasWorkingOutreach
        electricPropulsionImageView.isVisible = machine.canRunOnElectricity
        nonElectricPropulsionImageView.isVisible = machine.canRunOnFossilFuel

        iconImageView.loadImage(machine.thumbnailUrl, placeholder = R.drawable.img_machines_contour)
    }

}
