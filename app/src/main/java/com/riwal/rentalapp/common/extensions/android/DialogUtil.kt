package com.riwal.rentalapp.common.extensions.android

import android.app.Dialog

fun <T : Dialog> T.onShow(callback: (T) -> Unit): T {
    this.setOnShowListener {
        callback(this)
    }
    return this
}