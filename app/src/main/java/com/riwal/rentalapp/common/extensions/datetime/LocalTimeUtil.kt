package com.riwal.rentalapp.common.extensions.datetime

import org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE
import org.joda.time.DateTimeConstants.MILLIS_PER_SECOND
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat

val LocalTime.isoString
    get() = ISO_TIME_FORMATTER.print(this)!!

val LocalTime.millisOfHour
    get() = this.minuteOfHour * MILLIS_PER_MINUTE + this.secondOfMinute * MILLIS_PER_SECOND + this.millisOfSecond

fun LocalTime.roundUpToNearestHour() = this
        .plusHours(if (millisOfHour == 0) 0 else 1)
        .withMinuteOfHour(0)
        .withSecondOfMinute(0)
        .withMillisOfSecond(0)!!

fun String.toLocalTime(): LocalTime? {
    return try {
        LocalTime.parse(this, ISODateTimeFormat.timeParser())
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun ClosedRange<String>.toLocalTimeRange(): ClosedRange<LocalTime>? {
    val from = start.toLocalTime()
    val to = endInclusive.toLocalTime()
    return if (from != null && to != null) from..to else null
}