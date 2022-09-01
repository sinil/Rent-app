package com.riwal.rentalapp.common.ui

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.*
import com.google.android.material.textfield.TextInputEditText
import com.riwal.rentalapp.common.extensions.widgets.TextWatcherAdapter
import com.riwal.rentalapp.common.extensions.android.clearFocusAndHideKeyboard

class BetterEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle) : TextInputEditText(context, attrs, defStyleAttr) {

    var onImeBackListener: (() -> Unit)? = null
    var onTextChangedListener: ((String) -> Unit)? = null
    var onFocusChangeListener: ((View, Boolean) -> Unit)? = null
    var onEditorActionListener: ((Int, KeyEvent?) -> Unit)? = null

    var text: String?
        get() = getText().toString()
        set(value) {
            if (text != (value ?: "")) {
                setText(value)
            }
        }

    init {
        addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onTextChangedListener?.invoke(s.toString())
            }
        })

        setOnFocusChangeListener { view, hasFocus ->
            onFocusChangeListener?.invoke(view, hasFocus)
        }

        setOnEditorActionListener { _, actionId, event ->

            onEditorActionListener?.invoke(actionId, event)

            if (actionId in listOf(IME_ACTION_DONE, IME_ACTION_GO, IME_ACTION_SEARCH, IME_ACTION_SEND)) {
                clearFocusAndHideKeyboard()
            }

            onEditorActionListener != null
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            dispatchKeyEvent(event)
            if (onImeBackListener == null) {
                clearFocus()
            }
            onImeBackListener?.invoke()
            return false
        }
        return super.onKeyPreIme(keyCode, event)
    }

}