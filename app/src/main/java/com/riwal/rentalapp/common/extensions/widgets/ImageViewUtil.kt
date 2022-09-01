package com.riwal.rentalapp.common.extensions.widgets

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.riwal.rentalapp.common.extensions.android.graphics.inverse

var ImageView.animationDrawable: AnimatedVectorDrawableCompat?
    get() = drawable as? AnimatedVectorDrawableCompat
    set(value) = setImageDrawable(value)

fun ImageView.setTintFromResource(@ColorRes colorRes: Int) {
    imageTintList = resources.getColorStateList(colorRes)
}

fun ImageView.getPixel(x: Int, y: Int): Int? {
    val bitmap = (drawable as BitmapDrawable?)?.bitmap ?: return null
    if (x < 0 || y < 0 || x >= bitmap.width || y >= bitmap.height) return null
    val inverseImageTransform = imageMatrix?.inverse ?: return null
    val points = listOf(x.toFloat(), y.toFloat()).toFloatArray()
    inverseImageTransform.mapPoints(points)
    return bitmap.getPixel(points[0].toInt(), points[1].toInt())
}

fun AnimatedVectorDrawableCompat.onAnimationEnd(action: () -> Unit): AnimatedVectorDrawableCompat {
    registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable) {
            action()
        }
    })
    return this
}