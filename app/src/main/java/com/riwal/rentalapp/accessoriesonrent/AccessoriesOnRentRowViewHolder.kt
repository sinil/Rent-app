package com.riwal.rentalapp.accessoriesonrent

import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.widgets.TextStyle
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.resources
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.extensions.widgets.textStyle
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.RentalStatus
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_rental.*

class AccessoriesOnRentRowViewHolder(override val containerView: View) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    var style = Style.NORMAL
        set(value) {
            field = value
            checkBox.isVisible = (style == Style.SELECTION)
        }

    var isChecked
        get() = checkBox.isChecked
        set(value) {
            checkBox.isChecked = value
        }

    fun updateWith(accessories: AccessoriesOnRent) {

        val project = accessories.project
        val status = accessories.status
        val isOffRentDateFinal = accessories.isOffRentDateFinal
        val onRentDate = accessories.onRentDateTime.toLocalDate()
        val offRentDate = accessories.offRentDateTime!!.toLocalDate()


        iconImageView.setImageResource(R.drawable.img_machines_contour)
        iconImageView.setTintFromResource(R.color.material_icon_disabled)


        titleTextView.text = accessories.accessoryName
        fleetNumberTextView.text = accessories.fleetNumber
        projectNameTextView.text = project.name

        when (status) {
            RentalStatus.PENDING_DELIVERY -> {
                nextImportantEventKeyTextView.text = getString(R.string.key_rental_order_start_renting)
                nextImportantEventTextView.text = onRentDate.toMediumStyleString(context)
                nextImportantEventSection.isVisible = true
                nextImportantEventTextView.textStyle = TextStyle.REGULAR
                statusTextView.text = getString(R.string.order_status_pending_deliveries)
                statusTextView.setTextColor(resources.getColor(R.color.material_yellow_900))
            }
            RentalStatus.ON_RENT -> {
                nextImportantEventKeyTextView.text = getString(R.string.key_rental_order_stop_renting)
                nextImportantEventTextView.text = if (isOffRentDateFinal) offRentDate.toMediumStyleString(context) else getString(R.string.rental_order_stop_renting_date_tbd)
                nextImportantEventTextView.textStyle = if (isOffRentDateFinal) TextStyle.REGULAR else TextStyle.BOLD
                nextImportantEventSection.isVisible = true
                statusTextView.text = getString(R.string.order_status_current_rentals)
                statusTextView.setTextColor(resources.getColor(R.color.material_green_300))
            }
            RentalStatus.PENDING_PICKUP -> {
                nextImportantEventSection.isVisible = false
                statusTextView.text = getString(R.string.order_status_pending_pickup)
                statusTextView.setTextColor(resources.getColor(R.color.material_yellow_900))
            }
            RentalStatus.CLOSED -> {
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