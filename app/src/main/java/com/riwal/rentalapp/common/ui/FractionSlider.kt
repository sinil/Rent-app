package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.inflate
import kotlin.math.roundToInt

class FractionSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var value = 0.0
        set(value) {
            if (field == value) {
                return
            }
            field = value
            seekBar.progress = (value * seekBar.max).roundToInt()
            onValueChangedListener?.invoke(value)
        }

    private var onValueChangedListener: ((value: Double) -> Unit)? = null
    private val seekBar: SeekBar = inflate(R.layout.seekbar)

    init {

        addView(seekBar)

        seekBar.progress = 0
        seekBar.max = Int.MAX_VALUE

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.FractionSlider, 0, 0)
            value = styledAttributes.getFloat(R.styleable.FractionSlider_value, 0f).toDouble()
            styledAttributes.recycle()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    value = progress / seekBar.max.toDouble()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun setOnValueChangedListener(listener: ((value: Double) -> Unit)?) {
        onValueChangedListener = listener
    }

}