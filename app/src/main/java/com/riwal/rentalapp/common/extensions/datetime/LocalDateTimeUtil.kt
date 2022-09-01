package com.riwal.rentalapp.common.extensions.datetime

import org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE
import org.joda.time.DateTimeConstants.MILLIS_PER_SECOND
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat

val LocalDateTime.isoString
    get() = ISO_DATE_TIME_FORMATTER.print(this)!!

val LocalDateTime.isToday
    get() = this.toLocalDate().isToday

val LocalDateTime.isFuture
    get() = this > today.toLocalDateTimeAtStartOfDay()

val LocalDateTime.millisOfHour
    get() = this.minuteOfHour * MILLIS_PER_MINUTE + this.secondOfMinute * MILLIS_PER_SECOND + this.millisOfSecond

fun LocalDateTime.withDate(date: LocalDate) = this.withDate(date.year, date.monthOfYear, date.dayOfMonth)!!
fun LocalDateTime.withTime(time: LocalTime) = this.withTime(time.hourOfDay, time.minuteOfHour, time.secondOfMinute, time.millisOfSecond)!!

fun LocalDateTime.roundUpToNearestHour() = this
        .plusHours(if (millisOfHour == 0) 0 else 1)
        .withMinuteOfHour(0)
        .withSecondOfMinute(0)
        .withMillisOfSecond(0)!!

fun LocalDate.withTime(time: LocalTime) = this.toLocalDateTime(time)!!
fun LocalDate.toLocalDateTimeAtStartOfDay() = this.withTime("00:00".toLocalTime()!!)
fun LocalDate.toLocalDateTimeAtEndOfDay() = this.withTime("23:59:59".toLocalTime()!!)

fun ClosedRange<LocalDateTime>.toLocalDateRange() = start.toLocalDate()..endInclusive.toLocalDate()

fun String.toLocalDateTime(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, ISODateTimeFormat.dateTimeParser())
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}
