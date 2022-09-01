package com.riwal.rentalapp.common.extensions.datetime

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE
import org.joda.time.DateTimeConstants.MILLIS_PER_SECOND
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

val DateTime.startOfDay
    get() = this.withTimeAtStartOfDay()

val DateTime.endOfDay
    get() = this.withTime(23, 59, 59, 999)

val DateTime.isoString
    get() = ISO_DATE_TIME_FORMATTER.print(this)!!

val DateTime.isToday
    get() = this.toLocalDate().isToday

val today
    get() = LocalDate.now()!!

val tomorrow
    get() = today.plusDays(1)!!

val dayaftertomorrow
    get() = today.plusDays(2)

fun Long.toDateTime() = DateTime(this)

fun String.toDateTime(): DateTime? {
    return try {
        DateTime.parse(this, ISODateTimeFormat.dateTimeParser())
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

val DateTime.millisOfHour
    get() = this.minuteOfHour * MILLIS_PER_MINUTE + this.secondOfMinute * MILLIS_PER_SECOND + this.millisOfSecond

fun DateTime.roundUpToNearestHour() = this
        .plusHours(if (millisOfHour == 0) 0 else 1)
        .withMinuteOfHour(0)
        .withSecondOfMinute(0)
        .withMillisOfSecond(0)!!