package com.riwal.rentalapp.common.extensions.widgets

import android.widget.EditText

fun EditText.moveCursorToEnd() {
    setSelection(text.length)
}