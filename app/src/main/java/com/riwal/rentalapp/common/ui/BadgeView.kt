package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.isVisible
import kotlinx.android.synthetic.main.badge.view.*


class BadgeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    init {

        addSubview(R.layout.badge)


    }

    var number: Int = 0
        set(value) {
            field = value
            isVisible = (value > 0)
            textView.text = "$value"
        }

    var text: String = ""
    set(value) {
        field = value
        isVisible = field.isNotEmpty()
        textView.text = value
    }

}
