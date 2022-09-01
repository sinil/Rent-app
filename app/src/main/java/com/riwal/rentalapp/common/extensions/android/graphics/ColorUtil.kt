package com.riwal.rentalapp.common.extensions.android.graphics

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.riwal.rentalapp.common.extensions.core.lerp

@ColorInt
private fun colorWithAlpha(@ColorInt color: Int, @IntRange(from = 0, to = 255) alpha: Int): Int {
    return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
}

@ColorInt fun lerpColors(@ColorInt a: Int, @ColorInt b: Int, factor: Float): Int {
    return Color.argb(
            lerp(Color.alpha(a), Color.alpha(b), factor),
            lerp(Color.red(a), Color.red(b), factor),
            lerp(Color.green(a), Color.green(b), factor),
            lerp(Color.blue(a), Color.blue(b), factor)
    )
}