package com.riwal.rentalapp.common.extensions.datetime

import android.content.Context
import net.danlew.android.joda.DateUtils.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

val ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime()!!
val ISO_DATE_FORMATTER = ISODateTimeFormat.date()!!
val ISO_TIME_FORMATTER = ISODateTimeFormat.time()!!
val UTC_DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

const val DATE_SHORT_STYLE = FORMAT_SHOW_DATE or FORMAT_SHOW_YEAR or FORMAT_NUMERIC_DATE
const val DATE_MEDIUM_STYLE = FORMAT_SHOW_DATE or FORMAT_SHOW_YEAR or FORMAT_ABBREV_MONTH
const val DATE_LONG_STYLE = FORMAT_SHOW_DATE or FORMAT_SHOW_YEAR
const val DATE_FULL_STYLE = FORMAT_SHOW_DATE or FORMAT_SHOW_YEAR or FORMAT_SHOW_WEEKDAY

const val TIME_STYLE = FORMAT_SHOW_TIME or FORMAT_NO_NOON

const val DATE_TIME_SHORT_STYLE = DATE_SHORT_STYLE or TIME_STYLE
const val DATE_TIME_MEDIUM_STYLE = DATE_MEDIUM_STYLE or TIME_STYLE
const val DATE_TIME_LONG_STYLE = DATE_LONG_STYLE or TIME_STYLE
const val DATE_TIME_FULL_STYLE = DATE_FULL_STYLE or TIME_STYLE

const val DATE_SHORT_STYLE_NO_MONTH_DAY = DATE_SHORT_STYLE or FORMAT_NO_MONTH_DAY
const val DATE_MEDIUM_STYLE_NO_MONTH_DAY = DATE_MEDIUM_STYLE or FORMAT_NO_MONTH_DAY
const val DATE_LONG_STYLE_NO_MONTH_DAY = DATE_LONG_STYLE or FORMAT_NO_MONTH_DAY
const val DATE_FULL_STYLE_NO_MONTH_DAY = DATE_FULL_STYLE or FORMAT_NO_MONTH_DAY

const val MONTH_SHORT_STYLE = FORMAT_SHOW_DATE or FORMAT_NO_YEAR or FORMAT_NUMERIC_DATE or FORMAT_NO_MONTH_DAY
const val MONTH_MEDIUM_STYLE = FORMAT_SHOW_DATE or FORMAT_NO_YEAR or FORMAT_ABBREV_MONTH or FORMAT_NO_MONTH_DAY
const val MONTH_LONG_STYLE = FORMAT_SHOW_DATE or FORMAT_NO_YEAR or FORMAT_NO_MONTH_DAY

fun DateTime.format(context: Context, flags: Int) = this.toLocalDateTime().format(context, flags)
fun DateTime.toShortStyleString(context: Context) = this.toLocalDateTime().toShortStyleString(context)
fun DateTime.toMediumStyleString(context: Context) = this.toLocalDateTime().toMediumStyleString(context)
fun DateTime.toLongStyleString(context: Context) = this.toLocalDateTime().toLongStyleString(context)
fun DateTime.toFullStyleString(context: Context) = this.toLocalDateTime().toFullStyleString(context)

fun LocalDateTime.format(context: Context, flags: Int) = formatDateTime(context, this, flags)!!
fun LocalDateTime.toShortStyleString(context: Context) = format(context, DATE_TIME_SHORT_STYLE)
fun LocalDateTime.toMediumStyleString(context: Context) = format(context, DATE_TIME_MEDIUM_STYLE)
fun LocalDateTime.toLongStyleString(context: Context) = format(context, DATE_TIME_LONG_STYLE)
fun LocalDateTime.toFullStyleString(context: Context) = format(context, DATE_TIME_FULL_STYLE)

fun LocalDate.format(context: Context, flags: Int) = formatDateTime(context, this, flags)!!
fun LocalDate.toShortStyleString(context: Context) = format(context, DATE_SHORT_STYLE)
fun LocalDate.toMediumStyleString(context: Context) = format(context, DATE_MEDIUM_STYLE)
fun LocalDate.toLongStyleString(context: Context) = format(context, DATE_LONG_STYLE)
fun LocalDate.toFullStyleString(context: Context) = format(context, DATE_FULL_STYLE)

fun LocalDate.toShortStyleNoMonthDayString(context: Context) = format(context, DATE_SHORT_STYLE_NO_MONTH_DAY)
fun LocalDate.toMediumStyleNoMonthDayString(context: Context) = format(context, DATE_MEDIUM_STYLE_NO_MONTH_DAY)
fun LocalDate.toLongStyleNoMonthDayString(context: Context) = format(context, DATE_LONG_STYLE_NO_MONTH_DAY)

@JvmName("formatLocalDateRange") fun ClosedRange<LocalDate>.format(context: Context, flags: Int) = formatDateRange(context, start, endInclusive, flags)!!
@JvmName("localDateRangeToShortStyleString") fun ClosedRange<LocalDate>.toShortStyleString(context: Context) = format(context, DATE_SHORT_STYLE)
@JvmName("localDateRangeToMediumStyleString") fun ClosedRange<LocalDate>.toMediumStyleString(context: Context) = format(context, DATE_MEDIUM_STYLE)
@JvmName("localDateRangeToLongStyleString") fun ClosedRange<LocalDate>.toLongStyleString(context: Context) = format(context, DATE_LONG_STYLE)
@JvmName("localDateRangeToFullStyleString") fun ClosedRange<LocalDate>.toFullStyleString(context: Context) = format(context, DATE_FULL_STYLE)

fun LocalTime.format(context: Context, flags: Int) = formatDateTime(context, this, flags)!!
fun LocalTime.toShortStyleString(context: Context) = format(context, TIME_STYLE)

@JvmName("formatLocalTimeRange") fun ClosedRange<LocalTime>.format(context: Context, flags: Int) = formatDateRange(context, start, endInclusive, flags)!!
@JvmName("localTimeRangeToShortStyleString") fun ClosedRange<LocalTime>.toShortStyleString(context: Context) = format(context, TIME_STYLE)
@JvmName("formatDateTimeRange") fun ClosedRange<DateTime>.format(context: Context, flags: Int) = formatDateRange(context, start, endInclusive, flags)!!
@JvmName("dateTimeRangeToShortStyleString") fun ClosedRange<DateTime>.toShortStyleString(context: Context) = format(context, DATE_TIME_SHORT_STYLE)
@JvmName("dateTimeRangeToMediumStyleString") fun ClosedRange<DateTime>.toMediumStyleString(context: Context) = format(context, DATE_TIME_MEDIUM_STYLE)
@JvmName("dateTimeRangeToLongStyleString") fun ClosedRange<DateTime>.toLongStyleString(context: Context) = format(context, DATE_TIME_LONG_STYLE)
@JvmName("dateTimeRangeToFullStyleString") fun ClosedRange<DateTime>.toFullStyleString(context: Context) = format(context, DATE_TIME_FULL_STYLE)