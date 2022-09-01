package com.riwal.rentalapp.model

import com.riwal.rentalapp.common.extensions.datetime.*
import org.joda.time.DateTime.now
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

class ValidOnRentPeriodCalculator(
        val nextDayDeliveryCutoffTime: LocalTime,
        val weekend: List<DayOfWeek>
) {


    fun earliestValidOnRentDateTime(): LocalDateTime {
        val currentTime = now().toLocalTime()
        val earliestOnRentDate = when {
            today.isWeekday && currentTime <= nextDayDeliveryCutoffTime && !tomorrow.isInWeekend -> tomorrow
            today.isWeekday && currentTime <= nextDayDeliveryCutoffTime && tomorrow.isInWeekend -> nextWorkday
            today.isInWeekend || tomorrow.isInWeekend -> nextWorkday.plusDays(1)
            today.isWeekday && currentTime > nextDayDeliveryCutoffTime && tomorrow.plusDays(1).isInWeekend -> nextWorkday.plusDays(3) // handle thursday after nextDayDeliveryCutoffTime
            else -> tomorrow.plusDays(1)
        }
        return earliestOnRentDate.toLocalDateTimeAtStartOfDay()
    }

    fun latestValidOnRentDateTime(offRentDate: LocalDate?, offRentTime: LocalTime?): LocalDateTime = when {
        offRentDate != null && offRentTime != null -> offRentDate.withTime(offRentTime) - 1.hours()
        offRentDate != null -> offRentDate.plusDays(1).toLocalDateTimeAtStartOfDay() - 1.hours()
        else -> DISTANT_FUTURE.toLocalDateTime()!!
    }

    fun validOnRentPeriod(offRentDate: LocalDate?, offRentTime: LocalTime?) = earliestValidOnRentDateTime()..latestValidOnRentDateTime(offRentDate, offRentTime)

    fun validOnRentTimeRange(onRentDate: LocalDate?, offRentDate: LocalDate?, offRentTime: LocalTime?): ClosedRange<LocalTime> {

        val validOnRentPeriod = validOnRentPeriod(offRentDate, offRentTime)
        val latestValidOnRentDate = validOnRentPeriod.endInclusive.toLocalDate()
        val latestValidOnRentTime = validOnRentPeriod.endInclusive.toLocalTime()

        return if (onRentDate == latestValidOnRentDate) {
            "00:00".toLocalTime()!!..latestValidOnRentTime
        } else {
            "00:00".toLocalTime()!!.."23:59".toLocalTime()!!
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private val nextWorkday
        get() = nextWorkdayAfter(today)

    private fun nextWorkdayAfter(date: LocalDate) = generateSequence(date.plusDays(1)) { it.plusDays(1) }
            .first { !it.isInWeekend }


    /*----------------------------------- Private extensions -------------------------------------*/


    private val LocalDate.isInWeekend
        get() = DayOfWeek.fromValue(dayOfWeek) in weekend

    private val LocalDate.isWeekday
        get() = !isInWeekend
}