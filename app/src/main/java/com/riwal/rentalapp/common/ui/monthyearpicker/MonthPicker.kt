package com.riwal.rentalapp.common.ui.monthyearpicker

import android.content.Context
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.DEFAULT_BOLD
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.runOnUiThread
import com.riwal.rentalapp.common.extensions.widgets.setTextColorFromResource
import com.riwal.rentalapp.common.extensions.datetime.*
import kotlinx.android.synthetic.main.view_month_year.view.*
import org.joda.time.DateTime.now
import org.joda.time.LocalDate

class MonthPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var minDate = DISTANT_PAST.toLocalDate()!!
        set(value) {
            field = value
            updateUI()
        }

    var maxDate = DISTANT_FUTURE.toLocalDate()!!
        set(value) {
            field = value
            updateUI()
        }

    var selectedDate: LocalDate? = null
        set(value) {
            field = value
            visibleYear = selectedDate?.year ?: now().year
            updateUI()
        }

    var onDateSelected: (date: LocalDate) -> Unit = { }

    private var visibleYear = selectedDate?.year ?: now().year
        set(value) {
            field = value
            updateUI()
        }

    private val monthButtons
        get() = listOf(jan, feb, mar, apr, may, jun, jul, aug, sep, okt, nov, dec)

    init {
        addSubview(R.layout.view_month_year)

        previousYearButton.setOnClickListener { visibleYear -= 1 }
        nextYearButton.setOnClickListener { visibleYear += 1 }

        monthButtons.forEach { button ->
            val monthOfYear = monthForButton(button)
            button.text = LocalDate(2000, monthOfYear, 1).format(context, MONTH_MEDIUM_STYLE) // Year and day of month are ignored
            button.setOnClickListener {
                selectedDate = LocalDate(visibleYear, monthOfYear, 1)
                onDateSelected(selectedDate!!)
            }
        }

        updateUI()
    }

    fun updateUI(animated: Boolean = true) = runOnUiThread {

        if (animated) {
            beginDelayedTransition()
        }

        selectionTextView.text = selectedDate?.toLongStyleNoMonthDayString(context)
        yearTextView.text = "$visibleYear"

        monthButtons.forEach { button ->
            val monthOfYear = monthForButton(button)
            val dateForMonth = LocalDate(visibleYear, monthOfYear, 1)
            val isSelectedMonth = dateForMonth == selectedDate?.withDayOfMonth(1)
            button.isEnabled = dateForMonth in minDate.withDayOfMonth(1)..maxDate.withDayOfMonth(1)
            button.setTextColorFromResource(if (isSelectedMonth) R.color.colorAccent else if (button.isEnabled) R.color.material_text_high_emphasis else R.color.material_text_disabled)
            button.typeface = if (isSelectedMonth) DEFAULT_BOLD else DEFAULT
        }
    }

    private fun monthForButton(button: Button) = monthButtons.indexOf(button) + 1
}