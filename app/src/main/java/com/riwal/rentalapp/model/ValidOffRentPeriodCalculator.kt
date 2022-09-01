package com.riwal.rentalapp.model

import com.riwal.rentalapp.common.extensions.datetime.*
import org.joda.time.DateTime.now
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

class ValidOffRentPeriodCalculator(val sameDayOffRentCutoffTime: LocalTime) {

    fun earliestValidOffRentDateTime(onRentDate: LocalDate?, onRentTime: LocalTime?): LocalDateTime {
        val currentTime = now().toLocalTime()
        val onRentDateTime = if (onRentDate == null || onRentTime == null) null else onRentDate.withTime(onRentTime)
        return when {
            onRentDateTime != null -> onRentDateTime + 1.hours()
            currentTime > sameDayOffRentCutoffTime -> tomorrow.toLocalDateTimeAtStartOfDay()
            else -> now().toLocalDateTime()
        }
    }

    fun latestValidOffRentDateTime() = DISTANT_FUTURE.toLocalDateTime()

    fun validOffRentPeriod(onRentDate: LocalDate? = null, onRentTime: LocalTime? = null) = earliestValidOffRentDateTime(onRentDate, onRentTime)..latestValidOffRentDateTime()

    fun validOffRentTimeRange(offRentDate: LocalDate?, onRentDate: LocalDate? = null, onRentTime: LocalTime? = null): ClosedRange<LocalTime> {

        val validOffRentPeriod = validOffRentPeriod(onRentDate, onRentTime)
        val earliestValidOffRentDate = validOffRentPeriod.start.toLocalDate()
        val earliestValidOffRentTime = validOffRentPeriod.start.toLocalTime()

        return if (offRentDate == earliestValidOffRentDate) {
            earliestValidOffRentTime.."23:59".toLocalTime()!!
        } else {
            "00:00".toLocalTime()!!.."23:59".toLocalTime()!!
        }
    }
}