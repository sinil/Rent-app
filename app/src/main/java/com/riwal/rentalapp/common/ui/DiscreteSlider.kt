package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.inflate

class DiscreteSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var value = 0
        set(value) {
            if (field == value) {
                return
            }
            field = value
            seekBar.progress = value
            onValueChangedListener?.invoke(value)
        }

    var max
        get() = seekBar.max
        set(value) {
            seekBar.max = value
        }

    private var onValueChangedListener: ((value: Int) -> Unit)? = null
    private val seekBar: SeekBar = inflate(R.layout.seekbar)

    init {

        addView(seekBar)

        seekBar.progress = 0

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.DiscreteSlider, 0, 0)
            value = styledAttributes.getInt(R.styleable.DiscreteSlider_value, 0)
            max = styledAttributes.getInt(R.styleable.DiscreteSlider_max, 0)
            styledAttributes.recycle()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    value = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun setOnValueChangedListener(listener: ((value: Int) -> Unit)?) {
        onValueChangedListener = listener
    }

}