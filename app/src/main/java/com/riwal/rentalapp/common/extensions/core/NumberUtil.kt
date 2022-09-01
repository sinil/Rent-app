package com.riwal.rentalapp.common.extensions.core

import org.joda.time.Hours
import org.joda.time.Months
import org.joda.time.Weeks
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

val Int.isEven
    get() = this % 2 == 0

val Int.isOdd
    get() = !isEven

fun Int.Companion.random() = Random().nextInt()

fun Double.round() = round(this)
fun Double.roundDown() = floor(this)
fun Double.roundUp() = ceil(this)

fun Float.round() = round(this)
fun Float.roundDown() = floor(this)
fun Float.roundUp() = ceil(this)

val Float.Companion.PI
    get() = kotlin.math.PI.toFloat()

fun Float.toDegrees() = this.toDouble().toDegrees().toFloat()
fun Float.toRadians() = this.toDouble().toRadians().toFloat()

fun Double.toDegrees() = Math.toDegrees(this)
fun Double.toRadians() = Math.toRadians(this)

fun lerp(a: Float, b: Float, fraction: Float) = a * (1 - fraction) + b * fraction
fun lerp(a: Int, b: Int, fraction: Float) = lerp(a.toFloat(), b.toFloat(), fraction).toInt()

fun linearResponse(value: Int, start: Int, end: Int) = linearResponse(value.toFloat(), start.toFloat(), end.toFloat())
fun linearResponse(value: Float, start: Float, end: Float) = (value.clampTo(start..end) - start) / (end - start)

fun Int.linearResponseIn(range: IntRange) = linearResponse(this.toFloat(), range.start.toFloat(), range.endInclusive.toFloat())
fun Float.linearResponseIn(range: FloatRange) = linearResponse(this, range.start, range.endInclusive)

fun Int.toHours() = Hours.hours(this)!!
fun Int.months() = Months.months(this)!!
fun Int.week() = Weeks.weeks(this)!!

fun Float.format(minimumFractionDigits: Int = 0, maximumFractionDigits: Int = 149 /* maximum a float can represent */): String {
    val formatter = NumberFormat.getNumberInstance().apply {
        this.minimumFractionDigits = minimumFractionDigits
        this.maximumFractionDigits = maximumFractionDigits
    }
    return formatter.format(this)
}

fun Float.formatThousandSeparator(): String {
    val decimal = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.getDefault()))
    return decimal.format(this).toString()
}

fun Float.formatWithDigits(fractionDigits: Int) = format(minimumFractionDigits = fractionDigits, maximumFractionDigits = fractionDigits)

fun Int.format() = NumberFormat.getIntegerInstance().format(this)!!

infix fun Boolean.implies(q: Boolean) = !this || q