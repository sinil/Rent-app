package com.riwal.rentalapp.common.extensions.datetime

import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

val LocalDate.isoString
    get() = ISO_DATE_FORMATTER.print(this)!!

val LocalDate.isToday
    get() = this == today

fun LocalDate.withLastDayOfMonth() = this.withDayOfMonth(1).plusMonths(1).minusDays(1)!!

fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, ISODateTimeFormat.dateParser())
    } catch (e: IllegalArgumentException) {
        null
    }
}
