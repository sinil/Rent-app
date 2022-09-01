package com.riwal.rentalapp.common.extensions.android.graphics

import android.graphics.Matrix

val Matrix.inverse: Matrix?
    get() {
        val inverse = Matrix()
        val success = this.invert(inverse)
        return if (success) inverse else null
    }