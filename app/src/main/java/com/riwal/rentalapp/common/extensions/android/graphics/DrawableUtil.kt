package com.riwal.rentalapp.common.extensions.android.graphics

import android.graphics.drawable.Drawable
import android.util.SizeF

val Drawable.size
    get() = SizeF(intrinsicWidth.toFloat(), intrinsicHeight.toFloat())