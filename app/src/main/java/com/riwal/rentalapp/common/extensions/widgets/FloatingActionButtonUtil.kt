package com.riwal.rentalapp.common.extensions.widgets

import androidx.annotation.ColorRes
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.setBackgroundTint(@ColorRes colorRes: Int) {
    backgroundTintList = resources.getColorStateList(colorRes)
}