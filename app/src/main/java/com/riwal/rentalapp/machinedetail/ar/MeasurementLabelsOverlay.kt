package com.riwal.rentalapp.machinedetail.ar

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import com.riwal.rentalapp.common.Vector2
import com.riwal.rentalapp.common.directionTo
import com.riwal.rentalapp.common.extensions.android.center
import com.riwal.rentalapp.common.extensions.android.layoutHeight
import com.riwal.rentalapp.common.extensions.android.layoutWidth
import com.riwal.rentalapp.common.extensions.core.toDegrees
import com.riwal.rentalapp.common.extensions.widgets.TextStyle.BOLD
import com.riwal.rentalapp.common.extensions.widgets.textStyle
import com.riwal.rentalapp.common.lerp
import com.riwal.rentalapp.common.toPoint
import kotlin.math.atan2

class MeasurementLabelsOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    /*---------------------------------------- Properties ----------------------------------------*/


    var widthText = ""
        set(value) {
            field = value
            updateUI()
        }

    var heightText = ""
        set(value) {
            field = value
            updateUI()
        }

    private var widthTextView = TextView(context)
    private var heightTextView = TextView(context)


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    init {
        initialize()
    }


    /*------------------------------------------ Methods -----------------------------------------*/


    fun centerAndOrientWidthTextViewBetweenPoints(pointA: Vector2, pointB: Vector2) = centerAndOrientTextViewBetweenPoints(widthTextView, pointA, pointB)
    fun centerAndOrientHeightTextViewBetweenPoints(pointA: Vector2, pointB: Vector2) = centerAndOrientTextViewBetweenPoints(heightTextView, pointA, pointB)


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun initialize() {

        addView(widthTextView)
        widthTextView.setTextSize(COMPLEX_UNIT_DIP, 16f)
        widthTextView.textStyle = BOLD
        widthTextView.layoutWidth = WRAP_CONTENT
        widthTextView.layoutHeight = WRAP_CONTENT

        addView(heightTextView)
        heightTextView.setTextSize(COMPLEX_UNIT_DIP, 16f)
        heightTextView.textStyle = BOLD
        heightTextView.layoutWidth = WRAP_CONTENT
        heightTextView.layoutHeight = WRAP_CONTENT

    }

    private fun updateUI() {
        widthTextView.text = widthText
        heightTextView.text = heightText
    }

    private fun centerAndOrientTextViewBetweenPoints(textView: TextView, pointA: Vector2, pointB: Vector2) {
        val direction = pointA.directionTo(pointB)
        val angle = atan2(direction.y, direction.x)
        textView.rotation = angle.toDegrees()// + 180
        textView.center = lerp(pointA, pointB, 0.5f).toPoint()
    }

}