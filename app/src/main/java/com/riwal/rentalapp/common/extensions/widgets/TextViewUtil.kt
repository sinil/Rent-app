package com.riwal.rentalapp.common.extensions.widgets

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.annotation.ColorRes

abstract class TextWatcherAdapter : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}

val TextView.isEllipsized
    get() = (layout?.text ?: "") != text

fun TextView.setBackgroundTint(@ColorRes colorRes: Int) {
    backgroundTintList = resources.getColorStateList(colorRes)
}

fun TextView.setTextColorFromResource(@ColorRes colorRes: Int) {
    setTextColor(resources.getColor(colorRes))
}

fun TextView.setDrawableTint(@ColorRes colorRes: Int) {
    compoundDrawablesRelative.filterNotNull().forEach {
        it.mutate().setColorFilter(context.resources.getColor(colorRes), PorterDuff.Mode.SRC_IN);
    }
}

enum class TextStyle(val rawValue: Int) {
    REGULAR(0),
    BOLD(1),
    ITALIC(2),
    BOLD_ITALIC(3);

    companion object {
        fun fromRawValue(rawValue: Int) = values().firstOrNull { it.rawValue == rawValue }
    }
}

var TextView.textStyle
    get() = TextStyle.fromRawValue(typeface.style)!!
    set(style) {
        val rawStyle = style.rawValue
        val typeface = Typeface.create(typeface, rawStyle)
        setTypeface(typeface, rawStyle)
    }
