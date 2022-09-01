package com.riwal.rentalapp.orderpanel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.transition.Slide
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.transitions
import com.riwal.rentalapp.common.extensions.datetime.tomorrow
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.interval
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onDateSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onTimeSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.extensions.widgets.setTextColorFromResource
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.*
import com.riwal.rentalapp.orderpanel.OrderPanelView.DataSource
import com.riwal.rentalapp.orderpanel.OrderPanelView.Delegate
import kotlinx.android.synthetic.main.panel_order_rental.view.*
import net.danlew.android.joda.DateUtils.FORMAT_SHOW_WEEKDAY
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class OrderPanelViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), OrderPanelView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    var isOpen = false
        private set

    private val defaultOnRentDate
        get() = dataSource.defaultOnRentDate(view = this)

    private val defaultOnRentTime
        get() = dataSource.defaultOnRentTime(view = this)

    private val defaultOffRentDate
        get() = dataSource.defaultOffRentDate(view = this)

    private val defaultOffRentTime
        get() = dataSource.defaultOffRentTime(view = this)

    private val validOnRentDateRange
        get() = dataSource.validOnRentDateRange(view = this)

    private val validTimeRangeForOnRentDate
        get() = dataSource.validTimeRangeForOnRentDate(view = this)

    private val validOffRentDateRange
        get() = dataSource.validOffRentDateRange(view = this)

    private val validTimeRangeForOffRentDate
        get() = dataSource.validTimeRangeForOffRentDate(view = this)

    private val onRentDate
        get() = dataSource.onRentDate(view = this)

    private val onRentTime
        get() = dataSource.onRentTime(view = this)

    private val offRentDateStrictness
        get() = dataSource.offRentDateStrictness(view = this)

    private val offRentDate
        get() = dataSource.offRentDate(view = this)

    private val offRentTime
        get() = dataSource.offRentTime(view = this)

    private val exactOffRentDateTime
        get() = if (offRentDate != null && offRentTime != null) offRentDate!!.toDateTime(offRentTime!!) else null

    private val canSelectOnRentTime
        get() = dataSource.canSelectOnRentTime(view = this)

    private val canSelectOffRentStrictness
        get() = dataSource.canSelectOffRentStrictness(view = this)

    private val canSelectOffRentDate
        get() = dataSource.canSelectOffRentDate(view = this)

    private val canSelectOffRentTime
        get() = dataSource.canSelectOffRentTime(view = this)

    private val canAddToOrder
        get() = dataSource.canAddToOrder(view = this)

    private val country
        get() = dataSource.country(view = this)

    private val canRentTomorrow
        get() = validOnRentDateRange.start <= tomorrow

    private val isAccess4UAvailable
        get() = country.isAccess4UAvailable

    private val firstDayOfWeek
        get() = country.firstDayOfWeek


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.panel_order_rental)

        onRentDateButton.setOnClickListener {
            showDatePicker(selectedDate = onRentDate ?: defaultOnRentDate, validRange = validOnRentDateRange) { date ->
                delegate.onOnRentDatePicked(date)
            }
        }

        onRentTimeButton.setOnClickListener {
            showTimePicker(selectedTime = onRentTime ?: defaultOnRentTime, validRange = validTimeRangeForOnRentDate) { time ->
                delegate.onOnRentTimePicked(time)
            }
        }

        offRentDateIsExactButton.setOnClickListener { delegate.onOffRentCertaintySelected(EXACT) }
        offRentDateIsApproximationButton.setOnClickListener { delegate.onOffRentCertaintySelected(APPROXIMATE) }

        exactOffRentDateButton.setOnClickListener {
            showDatePicker(selectedDate = offRentDate ?: defaultOffRentDate, validRange = validOffRentDateRange) { date ->
                delegate.onOffRentDatePicked(date)
            }
        }

        offRentTimeButton.setOnClickListener {
            showTimePicker(selectedTime = offRentTime ?: defaultOffRentTime, validRange = validTimeRangeForOffRentDate) { time ->
                delegate.onOffRentTimePicked(time)
            }
        }

        approximateOffRentDateButton.setOnClickListener {
            showDatePicker(selectedDate = offRentDate ?: defaultOffRentDate, validRange = validOffRentDateRange) { date ->
                delegate.onOffRentDatePicked(date)
            }
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

    override fun notifyCannotStartRentInWeekend() {
        AlertDialog.Builder(activity)
                .setMessage(R.string.notification_for_delivery_is_not_available_in_weekend)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    showDatePicker(selectedDate = onRentDate
                            ?: defaultOnRentDate, validRange = validOnRentDateRange) { date ->
                        delegate.onOnRentDatePicked(date)
                    }
                }
                .show()
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

        // On rent section


        val onRentDateString = onRentDate?.toMediumStyleString()
        onRentDateButtonTextView.text = onRentDateString ?: getString(R.string.rental_panel_date_button_placeholder)
        onRentDateButtonTextView.setTextColorFromResource(if (onRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)
        onRentDateButtonImageView.setTintFromResource(if (onRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)

        val onRentTimeString = onRentTime?.toShortStyleString()
        val onRentTimeColor = when {
            !canSelectOnRentTime -> R.color.material_text_disabled
            onRentTimeString == null -> R.color.colorAccent
            else -> R.color.material_text_high_emphasis
        }
        onRentTimeButton.isEnabled = canSelectOnRentTime
        onRentTimeButtonTextView.text = onRentTimeString ?: getString(R.string.rental_panel_time_button_placeholder)
        onRentTimeButtonTextView.setTextColorFromResource(onRentTimeColor)
        onRentTimeButtonImageView.setTintFromResource(onRentTimeColor)

        val earliestOnRentDate = validOnRentDateRange.start
        earliestOnRentDayNoticeTextView.text = getString(R.string.order_panel_earliest_delivery_day_notice, earliestOnRentDate.format(FORMAT_SHOW_WEEKDAY))
        earliestOnRentDayNoticeTextView.isVisible = !canRentTomorrow && onRentDate == null

        // Off rent strictness

        offRentDateStrictnessSection.isVisible = canSelectOffRentStrictness

        val offRentDateIsUnspecified = offRentDateStrictness == UNSPECIFIED
        offRentDateIsExactButton.setTextColorFromResource(if (offRentDateIsUnspecified) R.color.colorAccent else R.color.material_text_high_emphasis)
        offRentDateIsExactButton.backgroundColor = if (offRentDateStrictness == EXACT) color(R.color.material_background) else color(R.color.white)

        offRentDateIsApproximationButton.setTextColorFromResource(if (offRentDateIsUnspecified) R.color.colorAccent else R.color.material_text_high_emphasis)
        offRentDateIsApproximationButton.backgroundColor = if (offRentDateStrictness == APPROXIMATE) color(R.color.material_background) else color(R.color.white)

        // Exact off rent section

        val exactOffRentDateString = offRentDate?.toMediumStyleString()
        exactOffRentDateSection.isVisible = offRentDateStrictness == EXACT && canSelectOffRentDate
        exactOffRentDateButtonTextView.text = exactOffRentDateString ?: getString(R.string.rental_panel_date_button_placeholder)
        exactOffRentDateButtonTextView.setTextColorFromResource(if (exactOffRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)
        exactOffRentDateButtonImageView.setTintFromResource(if (exactOffRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)

        val offRentTimeString = offRentTime?.toShortStyleString()
        val offRentTimeColor = when {
            !canSelectOffRentTime -> R.color.material_text_disabled
            offRentTimeString == null -> R.color.colorAccent
            else -> R.color.material_text_high_emphasis
        }
        offRentTimeButton.isEnabled = canSelectOffRentTime
        offRentTimeButtonTextView.text = offRentTimeString ?: getString(R.string.rental_panel_time_button_placeholder)
        offRentTimeButtonTextView.setTextColorFromResource(offRentTimeColor)
        offRentTimeButtonImageView.setTintFromResource(offRentTimeColor)

        val exactOffRentDateStringForExplanation = exactOffRentDateTime?.toLongStyleString() ?: offRentDate?.toLongStyleString() ?: ""
        exactOffRentDateExplanationTextView.isVisible = (offRentDateStrictness == EXACT && offRentDate != null)
        exactOffRentDateExplanationTextView.text = getString(if (isAccess4UAvailable) R.string.order_panel_off_rent_exact_explanation else R.string.order_panel_off_rent_exact_explanation_no_access4u, exactOffRentDateStringForExplanation)

        // Approximate off rent section

        val approximateOffRentDateString = offRentDate?.toMediumStyleString()
        approximateOffRentDateSection.isVisible = offRentDateStrictness == APPROXIMATE && canSelectOffRentDate
        approximateOffRentDateButtonTextView.text = approximateOffRentDateString ?: getString(R.string.rental_panel_date_button_placeholder)
        approximateOffRentDateButtonTextView.setTextColorFromResource(if (approximateOffRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)
        approximateOffRentDateButtonImageView.setTintFromResource(if (approximateOffRentDateString == null) R.color.colorAccent else R.color.material_text_high_emphasis)

        approximateOffRentDateExplanationTextView.isVisible = (offRentDateStrictness == APPROXIMATE && offRentDate != null)
        approximateOffRentDateExplanationTextView.text = getString(if (isAccess4UAvailable) R.string.order_panel_off_rent_approximate_explanation else R.string.order_panel_off_rent_approximate_explanation_no_access4u, approximateOffRentDateString)

        // Confirm
        confirmButton.isEnabled = canAddToOrder

        orderRentalPanelOverlay.isVisible = isOpen
        bottomSheet.isVisible = isOpen
    }
}
