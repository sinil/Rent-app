package com.riwal.rentalapp.common.extensions.android

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt

fun Canvas.fillRect(rect: Rect, @ColorInt color: Int) {
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = color
    drawRect(rect, paint)
}