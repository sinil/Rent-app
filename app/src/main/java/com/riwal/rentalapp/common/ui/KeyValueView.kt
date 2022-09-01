package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.riwal.rentalapp.R
import kotlinx.android.synthetic.main.key_value_view.view.*

class KeyValueView : LinearLayout {


    /*--------------------------------------- Properties -----------------------------------------*/


    var value: String?
        get() = valueTextView.text.toString()
        set(value) {
            valueTextView.text = value
        }


    /*-------------------------------------- Constructors ----------------------------------------*/


    constructor(context: Context) : super(context) {
        initialize(context, attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initialize(context, attrs = attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {

        LayoutInflater.from(context).inflate(R.layout.key_value_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.KeyValueView, 0, 0)
            keyTextView.text = a.getText(R.styleable.KeyValueView_keyLabel)
            valueTextView.text = a.getText(R.styleable.KeyValueView_valueLabel)
            a.recycle()
        }
    }

}