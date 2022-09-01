package com.riwal.rentalapp.common.extensions.materialdatetimepicker

import com.riwal.rentalapp.common.extensions.datetime.DISTANT_FUTURE
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST
import com.riwal.rentalapp.common.extensions.datetime.toLocalTime
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import org.joda.time.LocalDate
import org.joda.time.LocalTime

fun DatePickerDialog.onDateSet(callback: (date: LocalDate) -> Unit): DatePickerDialog {

    setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        val date = LocalDate(year, monthOfYear + 1, dayOfMonth)
        callback(date)
    }
    return this
}

fun DatePickerDialog.firstDayOfWeek(startOfWeek: Int): DatePickerDialog {
    firstDayOfWeek = startOfWeek
    return this
}

fun DatePickerDialog.range(min: LocalDate? = null, max: LocalDate? = null): DatePickerDialog {

    minDate = (min?.toDateTimeAtStartOfDay() ?: DISTANT_PAST).toGregorianCalendar()
    maxDate = (max?.toDateTimeAtStartOfDay() ?: DISTANT_FUTURE).toGregorianCalendar()

    return this
}

fun DatePickerDialog.range(range: ClosedRange<LocalDate>) = range(min = range.start, max = range.endInclusive)

fun DatePickerDialog.vibrateOnChange(enable: Boolean): DatePickerDialog {
    vibrate(enable)
    return this
}

fun DatePickerDialog.title(title: String): DatePickerDialog {
    setTitle(title)
    return this
}

fun TimePickerDialog.onTimeSet(callback: (time: LocalTime) -> Unit): TimePickerDialog {

    setOnTimeSetListener { _, hourOfDay, minute, _ ->
        val time = LocalTime(hourOfDay, minute)
        callback(time)
    }
    return this
}

fun TimePickerDialog.range(min: LocalTime? = null, max: LocalTime? = null): TimePickerDialog {
    setMinTime(min ?: "00:00".toLocalTime()!!)
    setMaxTime(max ?: "23:59".toLocalTime()!!)
    return this
}

fun TimePickerDialog.range(range: ClosedRange<LocalTime>) = range(min = range.start, max = range.endInclusive)

fun TimePickerDialog.setMinTime(min: LocalTime?): TimePickerDialog {
    setMinTime((min ?: LocalTime(0, 0, 0)).toTimePoint())
    return this
}

fun TimePickerDialog.setMaxTime(max: LocalTime?): TimePickerDialog {
    setMaxTime((max ?: LocalTime(23, 59, 59)).toTimePoint())
    return this
}

fun TimePickerDialog.interval(hours: Int = 1, minutes: Int = 60, seconds: Int = 60): TimePickerDialog {
    setTimeInterval(hours, minutes, seconds)
    enableSeconds(seconds in 1..59)
    return this
}

fun TimePickerDialog.vibrateOnChange(enable: Boolean): TimePickerDialog {
    vibrate(enable)
    return this
}

fun TimePickerDialog.title(title: String): TimePickerDialog {
    this.title = title
    return this
}

fun LocalTime.toTimePoint() = Timepoint(hourOfDay, minuteOfHour, secondOfMinute)