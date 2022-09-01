package com.riwal.rentalapp.common.extensions.material

import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.View

fun BottomSheetBehavior<View>.onSlide(handler: (slideOffset: Float) -> Unit) {
    setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            handler(slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {}
    })
}

fun BottomSheetBehavior<View>.onStateChanged(handler: (newState: Int) -> Unit) {
    setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            handler(newState)
        }
    })
}