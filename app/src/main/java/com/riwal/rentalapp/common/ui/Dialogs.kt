package com.riwal.rentalapp.common.ui

import android.content.Context
import android.content.DialogInterface.BUTTON_POSITIVE
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.onShow
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_FUTURE
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.firstDayOfWeek
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.vibrateOnChange
import com.riwal.rentalapp.common.ui.monthyearpicker.MonthPicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.*
import java.util.Calendar.*

object Dialogs {

    fun datePicker(date: LocalDate = DateTime.now().toLocalDate(), firstDayOfWeek: Int = MONDAY) = DatePickerDialog
            .newInstance(null, date.year, date.monthOfYear - 1, date.dayOfMonth)
            .firstDayOfWeek(firstDayOfWeek)
            .vibrateOnChange(false)
            .range()

    fun timePicker(time: LocalTime = DateTime.now().toLocalTime()) = TimePickerDialog
            .newInstance(null, time.hourOfDay, time.minuteOfHour, true)
            .vibrateOnChange(false)

    fun monthPicker(context: Context, selectedDate: LocalDate? = null, minDate: LocalDate? = null, maxDate: LocalDate? = null, callback: (selectedDate: LocalDate) -> Unit): AlertDialog {

        val picker = MonthPicker(context).apply {
            this.selectedDate = selectedDate
            this.minDate = minDate ?: DISTANT_PAST.toLocalDate()
            this.maxDate = maxDate ?: DISTANT_FUTURE.toLocalDate()
        }

        return AlertDialog.Builder(context)
                .setView(picker)
                .setPositiveButton(R.string.confirm) { _, _ -> callback(picker.selectedDate!!) }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
                .onShow { dialog ->
                    val confirmButton = dialog.getButton(BUTTON_POSITIVE)
                    confirmButton.isEnabled = picker.selectedDate != null
                    picker.onDateSelected = { confirmButton.isEnabled = true }
                }
    }

}
