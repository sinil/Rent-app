package com.riwal.rentalapp.common.extensions.datetime

import android.content.Context
import net.danlew.android.joda.DateUtils
import org.joda.time.Duration
import kotlin.math.roundToLong

fun Int.milliseconds() = this.toLong().milliseconds()
fun Int.seconds() = this.toLong().seconds()
fun Int.minutes() = this.toLong().minutes()
fun Int.hours() = this.toLong().hours()
fun Int.days() = this.toLong().days()

fun Long.milliseconds() = Duration(this)
fun Long.seconds() = Duration.standardSeconds(this)!!
fun Long.minutes() = Duration.standardMinutes(this)!!
fun Long.hours() = Duration.standardHours(this)!!
fun Long.days() = Duration.standardDays(this)!!

fun Double.milliseconds() = this.roundToLong().milliseconds()
fun Double.seconds() = (this * 1000).milliseconds()
fun Double.minutes() = (this * 60).seconds()
fun Double.hours() = (this * 60).minutes()
fun Double.days() = (this * 24).hours()

fun Duration.toShortStyleString() = DateUtils.formatElapsedTime(this)!!
fun Duration.toHumanizedString(context: Context) = DateUtils.formatDuration(context, this)!!
