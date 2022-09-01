package com.riwal.rentalapp.common.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.TextView
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.dp

open class IconTextView : TextView {


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

        compoundDrawablePadding = dp(8)

        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.IconTextView, 0, 0)
            val tintColor = a.getColor(R.styleable.IconTextView_drawableTint, context.resources.getColor(R.color.material_text_medium_emphasis))
            a.recycle()

            compoundDrawablesRelative.filterNotNull().forEach {
                it.mutate().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

}