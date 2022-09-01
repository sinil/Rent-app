package com.riwal.rentalapp.common.extensions.widgets

import android.widget.ScrollView
import androidx.core.widget.NestedScrollView


fun ScrollView.scrollToBottom(animated: Boolean = true) {
    val dx = 0
    val dy = height
    if (animated) {
        smoothScrollTo(dx, dy)
    } else {
        scrollTo(dx, dy)
    }
}

fun NestedScrollView.scrollToBottom(animated: Boolean = true) {
    val dx = 0
    val dy = height
    if (animated) {
        smoothScrollTo(dx, dy)
    } else {
        scrollTo(dx, dy)
    }
}