package com.riwal.rentalapp.offrentpanel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.transition.Slide
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.transitions
import com.riwal.rentalapp.common.extensions.datetime.today
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.interval
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onDateSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onTimeSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.extensions.widgets.setTextColorFromResource
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.offrentpanel.OffRentPanelController.Style.MULTIPLE_MACHINES
import com.riwal.rentalapp.offrentpanel.OffRentPanelView.DataSource
import com.riwal.rentalapp.offrentpanel.OffRentPanelView.Delegate
import kotlinx.android.synthetic.main.panel_off_rent.view.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime


class OffRentPanelViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), OffRentPanelView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    var isOpen = false
        private set

    private val defaultOffRentDate
        get() = dataSource.defaultOffRentDate(view = this)

    private val defaultOffRentTime
        get() = dataSource.defaultOffRentTime(view = this)

    private val validOffRentDateRange
        get() = dataSource.validOffRentDateRange(view = this)

    private val validTimeRangeForOffRentDate
        get() = dataSource.validTimeRangeForOffRentDate(view = this)

    private val offRentDate
        get() = dataSource.offRentDate(view = this)

    private val offRentTime
        get() = dataSource.offRentTime(view = this)

    private val offRentDateTime
        get() = if (offRentDate != null && offRentTime != null) offRentDate!!.toDateTime(offRentTime!!) else null

    private val style
        get() = dataSource.style(view = this)

    private val country
        get() = dataSource.country(view = this)

    private val canSelectOffRentTime
        get() = dataSource.canSelectOffRentTime(view = this)

    private val canConfirmOffRent
        get() = dataSource.canConfirmOffRent(view = this)

    private val isAccess4UAvailable
        get() = country.isAccess4UAvailable

    private val canOffRentToday
        get() = validOffRentDateRange.start == today

    private val firstDayOfWeek
        get() = country.firstDayOfWeek


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.panel_off_rent)

        offRentDateButton.setOnClickListener {
            showDatePicker(selectedDate = offRentDate
                    ?: defaultOffRentDate, validRange = validOffRentDateRange) { date ->
                delegate.onOffRentDatePicked(date)
            }
        }

        offRentTimeButton.setOnClickListener {
            showTimePicker(selectedTime = offRentTime
                    ?: defaultOffRentTime, validRange = validTimeRangeForOffRentDate) { time ->
                delegate.onOffRentTimePicked(time)
            }
        }
        
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioButtonYes) {
                delegate.pickupOptionChanged("Yes")
            } else {
                delegate.pickupOptionChanged("No")
            }
        })
        if (country == Country.DK) {
            delegate.pickupOptionChanged("Yes")
        }

        confirmButton.setOnClickListener { delegate.onConfirmSelected() }
        cancelButton.setOnClickListener { navigateBack() }
        orderRentalPanelOverlay.setOnClickListener { navigateBack() }
    }


    /*----------------------------------- OrderPanelView -----------------------------------*/


    override fun navigateBack() {
        isOpen = false
        updateUI()
    }

    override fun changeMachinePickupLocation() {
        confirmButton.isEnabled = canConfirmOffRent
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun show(parentView: ViewGroup, preparationHandler: ControllerPreparationHandler) {
        parentView.addView(this, controllerPreparationHandler = preparationHandler)
        isOpen = true
        updateUI()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun showDatePicker(selectedDate: LocalDate, validRange: ClosedRange<LocalDate>, callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker(selectedDate, firstDayOfWeek)
                .onDateSet(callback)
                .range(validRange)
                .show(supportFragmentManager, "DatePickerDialog")
    }

    private fun showTimePicker(selectedTime: LocalTime, validRange: ClosedRange<LocalTime>, callback: (time: LocalTime) -> Unit) {
        Dialogs.timePicker(selectedTime)
                .onTimeSet(callback)
                .interval(hours = 1)
                .range(validRange)
                .show(supportFragmentManager, "TimePickerDialog")
    }

    override fun updateUI(animated: Boolean) {

        if (animated) {

            val transitionSet = ParallelAutoTransition()
            val bottomSheetVisibilityWillChange = bottomSheet.isVisible != isOpen
            if (bottomSheetVisibilityWillChange) {
                transitionSet.transitions.forEach {
                    it?.excludeTarget(bottomSheet, true)
                }
                transitionSet.addTransition(Slide().addTarget(bottomSheet))
            }

            activity.beginDelayedTransition(transitionSet)
        }

        sameDayOffRentCutoffNoticeTextView.text = getString(R.string.off_rent_panel_same_day_off_rent_cutoff_notice, country.sameDayOffRentCutoffTime.toShortStyleString())
        sameDayOffRentCutoffNoticeTextView.isVisible = !canOffRentToday && offRentDate == null

        val offRentDateString = offRentDate?.toMediumStyleString()
        offRentDateButtonTextView.text = offRentDateString
                ?: getString(R.string.rental_panel_date_button_placeholder)
        offRentDateButtonTextView.setTextColorFromResource(if (offRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)
        offRentDateButtonImageView.setTintFromResource(if (offRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)

        val offRentTimeColor = when {
            !canSelectOffRentTime -> R.color.material_text_disabled
            offRentTime == null -> R.color.colorAccent
            else -> R.color.material_text_high_emphasis
        }
        offRentTimeButton.isEnabled = canSelectOffRentTime
        offRentTimeButtonTextView.text = offRentTime?.toShortStyleString()
                ?: getString(R.string.rental_panel_time_button_placeholder)
        offRentTimeButtonTextView.setTextColorFromResource(offRentTimeColor)
        offRentTimeButtonImageView.setTintFromResource(offRentTimeColor)

        val explanationString = offRentDateTime?.toLongStyleString()
                ?: offRentDate?.toLongStyleString() ?: ""
        val explanatonForAccess4UStringId = if (style == MULTIPLE_MACHINES) R.string.off_rent_panel_off_rent_multiple_machines_explanation else R.string.off_rent_panel_off_rent_single_machine_explanation
        explanationTextView.isVisible = offRentDate != null
        pickUpLocationLayout.isVisible = (country == Country.DK) && (offRentDate != null) && (offRentTime != null)
        explanationTextView.text = getString(if (isAccess4UAvailable) explanatonForAccess4UStringId else R.string.order_panel_off_rent_exact_explanation_no_access4u, explanationString)

        confirmButton.isEnabled = canConfirmOffRent

        orderRentalPanelOverlay.isVisible = isOpen
        bottomSheet.isVisible = isOpen

    }
}
