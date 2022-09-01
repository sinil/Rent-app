package com.riwal.rentalapp.common.extensions.android

import android.content.res.Resources
import android.util.TypedValue

fun Resources.dp(value: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)

fun Resources.dp(value: Int): Int = dp(value.toFloat()).toInt()