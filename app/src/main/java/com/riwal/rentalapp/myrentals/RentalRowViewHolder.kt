package com.riwal.rentalapp.myrentals

import android.annotation.SuppressLint
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.core.StringCapitalizationOptions.MID_SENTENCE
import com.riwal.rentalapp.common.extensions.core.StringCapitalizationOptions.NOUN
import com.riwal.rentalapp.common.extensions.core.withCapitalization
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.TextStyle.BOLD
import com.riwal.rentalapp.common.extensions.widgets.TextStyle.REGULAR
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.resources
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.extensions.widgets.textStyle
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.model.localizedMachineType
import com.riwal.rentalapp.myrentals.RentalRowViewHolder.Style.NORMAL
import com.riwal.rentalapp.myrentals.RentalRowViewHolder.Style.SELECTION
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_rental.*

class RentalRowViewHolder(override val containerView: View) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    var style = NORMAL
        set(value) {
            field = value
            checkBox.isVisible = (style == SELECTION)
        }

    var isChecked
        get() = checkBox.isChecked
        set(value) {
            checkBox.isChecked = value
        }

    @SuppressLint("SetTextI18n")
    fun updateWith(rental: Rental) {

        val machine = rental.machine
        val project = rental.project
        val status = rental.status
        val machineType = machine?.localizedMachineType(context) ?: rental.machineType
        val localCapitalizedMachineType = machineType.withCapitalization(resources.configuration.locale, setOf(MID_SENTENCE, NOUN))
        val isOffRentDateFinal = rental.isOffRentDateFinal
        val onRentDate = rental.onRentDateTime.toLocalDate()
        val offRentDate = rental.offRentDateTime!!.toLocalDate()

        if (machine?.thumbnailUrl != null) {
            iconImageView.setImageDrawable(null)
            iconImageView.loadImage(machine.thumbnailUrl, placeholder = R.drawable.img_machines_contour)
            iconImageView.imageTintList = null
        } else {
            iconImageView.setImageResource(R.drawable.img_machines_contour)
            iconImageView.setTintFromResource(R.color.material_icon_disabled)
        }

        titleTextView.text = "${rental.rentalType} $localCapitalizedMachineType"
        fleetNumberTextView.text = rental.fleetNumber
        projectNameTextView.text = project.name

        when (status) {
            PENDING_DELIVERY -> {
                nextImportantEventKeyTextView.text = getString(R.string.key_rental_order_start_renting)
                nextImportantEventTextView.text = onRentDate.toMediumStyleString(context)
                nextImportantEventSection.isVisible = true
                nextImportantEventTextView.textStyle = REGULAR
                statusTextView.text = getString(R.string.order_status_pending_deliveries)
                statusTextView.setTextColor(resources.getColor(R.color.material_yellow_900))
            }
            ON_RENT -> {
                nextImportantEventKeyTextView.text = getString(R.string.key_rental_order_stop_renting)
                nextImportantEventTextView.text = if (isOffRentDateFinal) offRentDate.toMediumStyleString(context) else getString(R.string.rental_order_stop_renting_date_tbd)
                nextImportantEventTextView.textStyle = if (isOffRentDateFinal) REGULAR else BOLD
                nextImportantEventSection.isVisible = true
                statusTextView.text = getString(R.string.order_status_current_rentals)
                statusTextView.setTextColor(resources.getColor(R.color.material_green_300))
            }
            PENDING_PICKUP -> {
                nextImportantEventSection.isVisible = false
                statusTextView.text = getString(R.string.order_status_pending_pickup)
                statusTextView.setTextColor(resources.getColor(R.color.material_yellow_900))
            }
            CLOSED -> {
                nextImportantEventSection.isVisible = false
                statusTextView.text = getString(R.string.order_status_closed_rentals)
                statusTextView.setTextColor(resources.getColor(R.color.material_text_medium_emphasis))
            }
        }

    }


    enum class Style {
        NORMAL,
        SELECTION
    }

}