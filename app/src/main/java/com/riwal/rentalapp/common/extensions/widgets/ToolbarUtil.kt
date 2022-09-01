package com.riwal.rentalapp.common.extensions.widgets

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar

fun Toolbar.setNavigationIcon(@DrawableRes iconRes: Int, @ColorInt tint: Int) {
    navigationIcon = resources.getDrawable(iconRes)
    navigationIcon!!.setTint(tint)
}